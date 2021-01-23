package org.ituns.network.core;

import android.text.TextUtils;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public final class Request {
    private String url;
    private Method method;
    private Map<String, List<String>> headers;
    private Body body;

    private Request() {}

    public String url() { return url; }

    public Method method() { return method; }

    public Map<String, List<String>> headers() { return headers; }

    public Body body() { return body; }

    public static UrlBuilder get() {
        return new UrlBuilder(Method.GET);
    }

    public static UrlBuilder head() {
        return new UrlBuilder(Method.HEAD);
    }

    public static BodyBuilder post() {
        return new BodyBuilder(Method.POST);
    }

    public static BodyBuilder delete() {
        return new BodyBuilder(Method.DELETE);
    }

    public static BodyBuilder put() {
        return new BodyBuilder(Method.PUT);
    }

    public static BodyBuilder patch() {
        return new BodyBuilder(Method.PATCH);
    }

    public abstract static class Builder {
        String url;
        Method method;
        Map<String, List<String>> headers;

        private Builder(Method method) {
            this.method = method;
            this.headers = new HashMap<>();
        }

        public abstract Request build();
    }

    public static final class UrlBuilder extends Builder {
        Map<String, String> params;

        private UrlBuilder(Method method) {
            super(method);
            this.params = new HashMap<>();
        }

        public UrlBuilder url(String url) {
            this.url = url;
            return this;
        }

        public UrlBuilder addHeader(String name, String value) {
            if(!TextUtils.isEmpty(name) && !TextUtils.isEmpty(value)) {
                List<String> values = headers.get(name);
                if(values == null) {
                    values = new ArrayList<>();
                    headers.put(name, values);
                }
                values.add(value);
            }
            return this;
        }

        public UrlBuilder setHeader(String name, String value) {
            if(!TextUtils.isEmpty(name) && !TextUtils.isEmpty(value)) {
                headers.remove(name);
                ArrayList<String> values = new ArrayList<>();
                values.add(value);
                headers.put(name, values);
            }
            return this;
        }

        public UrlBuilder headers(Map<String, List<String>> headers) {
            if(headers != null) {
                this.headers.putAll(headers);
            }
            return this;
        }

        public UrlBuilder addParam(String key, String value) {
            if(!TextUtils.isEmpty(key) && !TextUtils.isEmpty(value)) {
                this.params.put(key, value);
            }
            return this;
        }

        public UrlBuilder addParams(Map<String, String> parameters) {
            if(parameters != null) {
                this.params.putAll(parameters);
            }
            return this;
        }

        @Override
        public Request build() {
            Request request = new Request();
            request.url = buildValidUrl(url, parametersToForm(params));
            request.method = method;
            request.headers = headers;
            return request;
        }
    }

    public interface Sink {
        void write(byte[] bytes) throws IOException;

        void write(File file) throws IOException;

        void write(String content) throws IOException;

        void write(String boundary, List<MultiPart> parts) throws IOException;
    }

    public static abstract class Body {

        public abstract MediaType mediaType();

        public abstract long contentLength() throws IOException;

        public abstract void writeTo(Sink sink) throws IOException;

        public static Body create(final MediaType type, final byte[] bytes) {
            return new Body() {
                @Override
                public MediaType mediaType() {
                    return type;
                }

                @Override
                public long contentLength() throws IOException {
                    return bytes.length;
                }

                @Override
                public void writeTo(Sink sink) throws IOException {
                    sink.write(bytes);
                }
            };
        }

        public static Body create(final MediaType type, final File file) {
            return new Body() {
                @Override
                public MediaType mediaType() {
                    return type;
                }

                @Override
                public long contentLength() throws IOException {
                    return file.length();
                }

                @Override
                public void writeTo(Sink sink) throws IOException {
                    sink.write(file);
                }
            };
        }

        public static Body create(final MediaType type, final String content) {
            return new Body() {
                @Override
                public MediaType mediaType() {
                    return type;
                }

                @Override
                public long contentLength() throws IOException {
                    return content.length();
                }

                @Override
                public void writeTo(Sink sink) throws IOException {
                    sink.write(content);
                }
            };
        }
    }

    public static final class MultiBody extends Body {
        private String boundary;
        private MediaType type;
        private List<MultiPart> parts;

        private MultiBody(String boundary, MediaType type, List<MultiPart> parts) {
            this.boundary = boundary;
            this.type = type;
            this.parts = new ArrayList<>(parts);
        }

        @Override
        public MediaType mediaType() {
            return type;
        }

        @Override
        public long contentLength() throws IOException {
            return -1;
        }

        @Override
        public void writeTo(Sink sink) throws IOException {
            sink.write(boundary, parts);
        }

        public static MultiBody create(String boundary, MediaType type, List<MultiPart>  parts) {
            return new MultiBody(boundary, type, parts);
        }
    }

    public static final class MultiPart {
        private static final byte[] CRLF = {'\r', '\n'};
        private static final byte[] DASHDASH = {'-', '-'};

        private String name;
        private String filename;
        private Body body;

        private MultiPart(String name, String filename, Body body) {
            this.name = name;
            this.filename = filename;
            this.body = body;
        }

        public String name() {
            return name;
        }

        public String filename() {
            return filename;
        }

        public Body body() {
            return body;
        }

        public void writeTo(Sink sink, String boundary) throws IOException {
            sink.write(DASHDASH);
            sink.write(boundary);
            sink.write(CRLF);

            if(!TextUtils.isEmpty(name)) {
                sink.write("Content-Disposition: ");
                sink.write("form-data; name=");
                sink.write(quotedString(name));
                if(!TextUtils.isEmpty(filename)) {
                    sink.write("; filename=");
                    sink.write(quotedString(filename));
                }
                sink.write(CRLF);
            }

            MediaType mediaType = body.mediaType();
            if(mediaType != null) {
                sink.write("Content-Type: ");
                sink.write(mediaType.name());
                sink.write("CRLF");
            }

            long contentLength = body.contentLength();
            if(contentLength != -1) {
                sink.write("Content-Length: ");
                sink.write(String.valueOf(contentLength));
                sink.write(CRLF);
            }

            sink.write(CRLF);
            body.writeTo(sink);
            sink.write(CRLF);
        }

        private String quotedString(String str) {
            StringBuilder builder = new StringBuilder();
            builder.append('"');
            for(int i = 0, len = str.length(); i < len; i++) {
                char c = str.charAt(i);
                switch (c) {
                    case '\n':
                        builder.append("%0A");
                        break;
                    case '\r':
                        builder.append("%0D");
                        break;
                    case '"':
                        builder.append("%22");
                        break;
                    default:
                        builder.append(c);
                }
            }
            builder.append('"');
            return builder.toString();
        }

        public static MultiPart create(String name, String filename, Body body) {
            return new MultiPart(name, filename, body);
        }
    }

    public static final class BodyBuilder {
        Method method;

        private BodyBuilder(Method method) {
            this.method = method;
        }

        public RawBodyBuilder raw() {
            return new RawBodyBuilder(method);
        }

        public TextBodyBuilder text() {
            return new TextBodyBuilder(method);
        }

        public FormBodyBuilder form() {
            return new FormBodyBuilder(method);
        }

        public JsonBodyBuilder json() {
            return new JsonBodyBuilder(method);
        }

        public FileBodyBuilder file() {
            return new FileBodyBuilder(method);
        }

        public MultiBodyBuilder multi() {
            return new MultiBodyBuilder(method);
        }
    }

    public static final class RawBodyBuilder extends Builder {
        MediaType type;
        byte[] body;

        private RawBodyBuilder(Method method) {
            super(method);
            this.type = MediaType.parse("application/octet-stream");
        }

        public RawBodyBuilder url(String url) {
            this.url = url;
            return this;
        }

        public RawBodyBuilder addHeader(String name, String value) {
            if(!TextUtils.isEmpty(name) && !TextUtils.isEmpty(value)) {
                List<String> values = headers.get(name);
                if(values == null) {
                    values = new ArrayList<>();
                    headers.put(name, values);
                }
                values.add(value);
            }
            return this;
        }

        public RawBodyBuilder setHeader(String name, String value) {
            if(!TextUtils.isEmpty(name) && !TextUtils.isEmpty(value)) {
                headers.remove(name);
                ArrayList<String> values = new ArrayList<>();
                values.add(value);
                headers.put(name, values);
            }
            return this;
        }

        public RawBodyBuilder headers(Map<String, List<String>> headers) {
            if(headers != null) {
                this.headers.putAll(headers);
            }
            return this;
        }

        public RawBodyBuilder type(MediaType type) {
            this.type = type;
            return this;
        }

        public RawBodyBuilder body(byte[] body) {
            this.body = body;
            return this;
        }

        @Override
        public Request build() {
            Request request = new Request();
            request.url = url;
            request.method = method;
            request.headers = headers;
            request.body = Body.create(type, body);
            return request;
        }
    }

    public static final class TextBodyBuilder extends Builder {
        MediaType type;
        String body;

        private TextBodyBuilder(Method method) {
            super(method);
            this.type = MediaType.parse("text/plain");
        }

        public TextBodyBuilder url(String url) {
            this.url = url;
            return this;
        }

        public TextBodyBuilder addHeader(String name, String value) {
            if(!TextUtils.isEmpty(name) && !TextUtils.isEmpty(value)) {
                List<String> values = headers.get(name);
                if(values == null) {
                    values = new ArrayList<>();
                    headers.put(name, values);
                }
                values.add(value);
            }
            return this;
        }

        public TextBodyBuilder setHeader(String name, String value) {
            if(!TextUtils.isEmpty(name) && !TextUtils.isEmpty(value)) {
                headers.remove(name);
                ArrayList<String> values = new ArrayList<>();
                values.add(value);
                headers.put(name, values);
            }
            return this;
        }

        public TextBodyBuilder headers(Map<String, List<String>> headers) {
            if(headers != null) {
                this.headers.putAll(headers);
            }
            return this;
        }

        public TextBodyBuilder type(MediaType type) {
            this.type = type;
            return this;
        }

        public TextBodyBuilder body(String body) {
            this.body = body;
            return this;
        }

        @Override
        public Request build() {
            Request request = new Request();
            request.url = url;
            request.method = method;
            request.headers = headers;
            request.body = Body.create(type, body);
            return request;
        }
    }

    public static final class FormBodyBuilder extends Builder {
        MediaType type;
        Map<String, String> params;

        private FormBodyBuilder(Method method) {
            super(method);
            this.type = MediaType.parse("application/x-www-form-urlencoded");
            this.params = new HashMap<>();
        }

        public FormBodyBuilder url(String url) {
            this.url = url;
            return this;
        }

        public FormBodyBuilder addHeader(String name, String value) {
            if(!TextUtils.isEmpty(name) && !TextUtils.isEmpty(value)) {
                List<String> values = headers.get(name);
                if(values == null) {
                    values = new ArrayList<>();
                    headers.put(name, values);
                }
                values.add(value);
            }
            return this;
        }

        public FormBodyBuilder setHeader(String name, String value) {
            if(!TextUtils.isEmpty(name) && !TextUtils.isEmpty(value)) {
                headers.remove(name);
                ArrayList<String> values = new ArrayList<>();
                values.add(value);
                headers.put(name, values);
            }
            return this;
        }

        public FormBodyBuilder headers(Map<String, List<String>> headers) {
            if(headers != null) {
                this.headers.putAll(headers);
            }
            return this;
        }

        public FormBodyBuilder type(MediaType type) {
            this.type = type;
            return this;
        }

        public FormBodyBuilder addParam(String key, String value) {
            if(!TextUtils.isEmpty(key) && !TextUtils.isEmpty(value)) {
                this.params.put(key, value);
            }
            return this;
        }

        public FormBodyBuilder addParams(Map<String, String> parameters) {
            if(parameters != null) {
                this.params.putAll(parameters);
            }
            return this;
        }

        @Override
        public Request build() {
            Request request = new Request();
            request.url = url;
            request.method = method;
            request.headers = headers;
            request.body = Body.create(type, parametersToForm(params));
            return request;
        }
    }

    public static final class JsonBodyBuilder extends Builder {
        MediaType type;
        Map<String, String> params;

        private JsonBodyBuilder(Method method) {
            super(method);
            this.type = MediaType.parse("application/json");
            this.params = new HashMap<>();
        }

        public JsonBodyBuilder url(String url) {
            this.url = url;
            return this;
        }

        public JsonBodyBuilder addHeader(String name, String value) {
            if(!TextUtils.isEmpty(name) && !TextUtils.isEmpty(value)) {
                List<String> values = headers.get(name);
                if(values == null) {
                    values = new ArrayList<>();
                    headers.put(name, values);
                }
                values.add(value);
            }
            return this;
        }

        public JsonBodyBuilder setHeader(String name, String value) {
            if(!TextUtils.isEmpty(name) && !TextUtils.isEmpty(value)) {
                headers.remove(name);
                ArrayList<String> values = new ArrayList<>();
                values.add(value);
                headers.put(name, values);
            }
            return this;
        }

        public JsonBodyBuilder headers(Map<String, List<String>> headers) {
            if(headers != null) {
                this.headers.putAll(headers);
            }
            return this;
        }

        public JsonBodyBuilder type(MediaType type) {
            this.type = type;
            return this;
        }

        public JsonBodyBuilder addParam(String key, String value) {
            if(!TextUtils.isEmpty(key) && !TextUtils.isEmpty(value)) {
                this.params.put(key, value);
            }
            return this;
        }

        public JsonBodyBuilder addParams(Map<String, String> parameters) {
            if(parameters != null) {
                this.params.putAll(parameters);
            }
            return this;
        }

        @Override
        public Request build() {
            Request request = new Request();
            request.url = url;
            request.method = method;
            request.headers = headers;
            request.body = Body.create(type, parametersToJson(params));
            return request;
        }
    }

    public static final class FileBodyBuilder extends Builder {
        MediaType type;
        File body;

        private FileBodyBuilder(Method method) {
            super(method);
            this.type = MediaType.parse("text/plain");
        }

        public FileBodyBuilder url(String url) {
            this.url = url;
            return this;
        }

        public FileBodyBuilder addHeader(String name, String value) {
            if(!TextUtils.isEmpty(name) && !TextUtils.isEmpty(value)) {
                List<String> values = headers.get(name);
                if(values == null) {
                    values = new ArrayList<>();
                    headers.put(name, values);
                }
                values.add(value);
            }
            return this;
        }

        public FileBodyBuilder setHeader(String name, String value) {
            if(!TextUtils.isEmpty(name) && !TextUtils.isEmpty(value)) {
                headers.remove(name);
                ArrayList<String> values = new ArrayList<>();
                values.add(value);
                headers.put(name, values);
            }
            return this;
        }

        public FileBodyBuilder headers(Map<String, List<String>> headers) {
            if(headers != null) {
                this.headers.putAll(headers);
            }
            return this;
        }

        public FileBodyBuilder type(MediaType type) {
            this.type = type;
            return this;
        }

        public FileBodyBuilder body(File body) {
            this.body = body;
            return this;
        }

        @Override
        public Request build() {
            Request request = new Request();
            request.url = url;
            request.method = method;
            request.headers = headers;
            request.body = Body.create(type, body);
            return request;
        }
    }

    public static final class MultiBodyBuilder extends Builder {
        String boundary;
        MediaType type;
        List<MultiPart> parts;

        private MultiBodyBuilder(Method method) {
            super(method);
            this.type = MediaType.parse("multipart/form-data");
            this.boundary = UUID.randomUUID().toString();
            this.parts = new ArrayList<>();
        }

        public MultiBodyBuilder url(String url) {
            this.url = url;
            return this;
        }

        public MultiBodyBuilder addHeader(String name, String value) {
            if(!TextUtils.isEmpty(name) && !TextUtils.isEmpty(value)) {
                List<String> values = headers.get(name);
                if(values == null) {
                    values = new ArrayList<>();
                    headers.put(name, values);
                }
                values.add(value);
            }
            return this;
        }

        public MultiBodyBuilder setHeader(String name, String value) {
            if(!TextUtils.isEmpty(name) && !TextUtils.isEmpty(value)) {
                headers.remove(name);
                ArrayList<String> values = new ArrayList<>();
                values.add(value);
                headers.put(name, values);
            }
            return this;
        }

        public MultiBodyBuilder headers(Map<String, List<String>> headers) {
            if(headers != null) {
                this.headers.putAll(headers);
            }
            return this;
        }

        public MultiBodyBuilder boundary(String boundary) {
            this.boundary = boundary;
            return this;
        }

        public MultiBodyBuilder type(MediaType type) {
            this.type = type;
            return this;
        }

        public MultiBodyBuilder addPart(Body body) {
            return addPart(null, null, body);
        }

        public MultiBodyBuilder addPart(String name, byte[] value) {
            return addPart(name,  null, Body.create(null, value));
        }

        public MultiBodyBuilder addPart(String name,  String value) {
            return addPart(name,  null, Body.create(null, value));
        }

        public MultiBodyBuilder addPart(String name, File file) {
            return addPart(name, file.getName(), Body.create(null, file));
        }

        public MultiBodyBuilder addPart(String name, String filename, Body body) {
            return addPart(MultiPart.create(name, filename, body));
        }

        public MultiBodyBuilder addPart(MultiPart part) {
            if(part != null) {
                this.parts.add(part);
            }
            return this;
        }

        @Override
        public Request build() {
            Request request = new Request();
            request.url = url;
            request.method = method;
            request.headers = headers;
            request.body = MultiBody.create(boundary, type, parts);
            return request;
        }
    }

    private static String buildValidUrl(String url, String params) {
        if(TextUtils.isEmpty(url)) {
            return url;
        }

        if(TextUtils.isEmpty(params)) {
            return url;
        }

        if(!url.contains("?")) {
            return url + "?" + params;
        }

        if(url.endsWith("&")) {
            return url + params;
        } else {
            return url + "&" + params;
        }
    }

    private static String parametersToForm(Map<String, String> parameters) {
        StringBuilder builder = new StringBuilder();
        if(parameters != null) {
            for (String key : parameters.keySet()) {
                String value = parameters.get(key);
                if (builder.length() > 0) builder.append("&");
                builder.append(key).append("=").append(value);
            }
        }
        return builder.toString();
    }

    private static String parametersToJson(Map<String, String> parameters) {
        try {
            return new JSONObject(parameters).toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "{}";
        }
    }
}