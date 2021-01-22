package org.ituns.network.core.temp;

import android.text.TextUtils;

import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class Request {
    private String url;
    private Method method;
    private Map<String, String> headers;
    private Body body;

    private Request() {}

    public String url() { return url; }

    public Method method() { return method; }

    public Map<String, String> headers() { return headers; }

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
        Map<String, String> headers;

        private Builder(Method method) {
            this.method = method;
            this.headers = new HashMap<>();
        }

        public Builder url(String url) {
            this.url = url;
            return this;
        }

        public Builder header(String key, String value) {
            if(!TextUtils.isEmpty(key) && !TextUtils.isEmpty(value)) {
                this.headers.put(key, value);
            }
            return this;
        }

        public Builder headers(Map<String, String> headers) {
            if(headers != null) {
                this.headers.putAll(headers);
            }
            return this;
        }

        public abstract Request build();
    }

    public static class UrlBuilder extends Builder {
        Map<String, String> parameters;

        private UrlBuilder(Method method) {
            super(method);
            this.parameters = new HashMap<>();
        }

        public UrlBuilder parameter(String key, String value) {
            if(!TextUtils.isEmpty(key) && !TextUtils.isEmpty(value)) {
                this.parameters.put(key, value);
            }
            return this;
        }

        public UrlBuilder parameters(Map<String, String> parameters) {
            if(parameters != null) {
                this.parameters.putAll(parameters);
            }
            return this;
        }

        @Override
        public Request build() {
            Request request = new Request();
            request.url = buildValidUrl(url, parametersToForm(parameters));
            request.method = method;
            request.headers = headers;
            return request;
        }
    }

    public static class BodyBuilder {
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
    }

    public static class RawBodyBuilder extends Builder {
        MediaType type;
        byte[] body;

        private RawBodyBuilder(Method method) {
            super(method);
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

    public static class TextBodyBuilder extends Builder {
        MediaType type;
        String body;

        private TextBodyBuilder(Method method) {
            super(method);
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

    public static class FormBodyBuilder extends Builder {
        MediaType type;
        Map<String, String> params;

        private FormBodyBuilder(Method method) {
            super(method);
            this.params = new HashMap<>();
        }

        public FormBodyBuilder type(MediaType type) {
            this.type = type;
            return this;
        }

        public FormBodyBuilder add(String key, String value) {
            if(!TextUtils.isEmpty(key) && !TextUtils.isEmpty(value)) {
                this.params.put(key, value);
            }
            return this;
        }

        public FormBodyBuilder addAll(Map<String, String> parameters) {
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

    public static class JsonBodyBuilder extends Builder {
        MediaType type;
        Map<String, String> params;

        private JsonBodyBuilder(Method method) {
            super(method);
            this.params = new HashMap<>();
        }

        public JsonBodyBuilder type(MediaType type) {
            this.type = type;
            return this;
        }

        public JsonBodyBuilder add(String key, String value) {
            if(!TextUtils.isEmpty(key) && !TextUtils.isEmpty(value)) {
                this.params.put(key, value);
            }
            return this;
        }

        public JsonBodyBuilder addAll(Map<String, String> parameters) {
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

    public static class FileBodyBuilder extends Builder {
        MediaType type;
        File body;

        private FileBodyBuilder(Method method) {
            super(method);
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