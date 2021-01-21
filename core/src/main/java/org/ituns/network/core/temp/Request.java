package org.ituns.network.core.temp;

import android.text.TextUtils;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Request {
    private String url;
    private byte[] body;
    private Method method;
    private MediaType mediaType;
    private Map<String, String> headers;

    private Request() {}

    public String url() { return url; }

    public byte[] body() { return body; }

    public Method method() { return method; }

    public MediaType mediaType() { return mediaType; }

    public Map<String, String> headers() { return headers; }

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

        public FormBodyBuilder form() {
            return new FormBodyBuilder(method);
        }

        public JsonBodyBuilder json() {
            return new JsonBodyBuilder(method);
        }
    }

    public static class RawBodyBuilder extends Builder {
        byte[] body;
        MediaType mediaType;

        private RawBodyBuilder(Method method) {
            super(method);
        }

        public RawBodyBuilder body(String body) {
            return this.body(body.getBytes());
        }

        public RawBodyBuilder body(byte[] body) {
            this.body = body;
            return this;
        }

        public RawBodyBuilder mediaType(MediaType mediaType) {
            this.mediaType = mediaType;
            return this;
        }

        @Override
        public Request build() {
            Request request = new Request();
            request.url = url;
            request.body = body;
            request.method = method;
            request.mediaType = mediaType;
            request.headers = headers;
            return request;
        }
    }

    public static class FormBodyBuilder extends Builder {
        MediaType mediaType;
        Map<String, String> parameters;

        private FormBodyBuilder(Method method) {
            super(method);
            this.parameters = new HashMap<>();
        }

        public FormBodyBuilder mediaType(MediaType mediaType) {
            this.mediaType = mediaType;
            return this;
        }

        public FormBodyBuilder parameter(String key, String value) {
            if(!TextUtils.isEmpty(key) && !TextUtils.isEmpty(value)) {
                this.parameters.put(key, value);
            }
            return this;
        }

        public FormBodyBuilder parameters(Map<String, String> parameters) {
            if(parameters != null) {
                this.parameters.putAll(parameters);
            }
            return this;
        }

        @Override
        public Request build() {
            Request request = new Request();
            request.url = url;
            request.body = parametersToForm(parameters).getBytes();
            request.method = method;
            request.mediaType = mediaType;
            request.headers = headers;
            return request;
        }
    }

    public static class JsonBodyBuilder extends Builder {
        MediaType mediaType;
        Map<String, String> parameters;

        private JsonBodyBuilder(Method method) {
            super(method);
            this.parameters = new HashMap<>();
        }

        public JsonBodyBuilder mediaType(MediaType mediaType) {
            this.mediaType = mediaType;
            return this;
        }

        public JsonBodyBuilder parameter(String key, String value) {
            if(!TextUtils.isEmpty(key) && !TextUtils.isEmpty(value)) {
                this.parameters.put(key, value);
            }
            return this;
        }

        public JsonBodyBuilder parameters(Map<String, String> parameters) {
            if(parameters != null) {
                this.parameters.putAll(parameters);
            }
            return this;
        }

        @Override
        public Request build() {
            Request request = new Request();
            request.url = url;
            request.body = parametersToJson(parameters).getBytes();
            request.method = method;
            request.mediaType = mediaType;
            request.headers = headers;
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