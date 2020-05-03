package org.ituns.network.core;

import android.text.TextUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NetworkResponse {
    private int code;
    private byte[] body;
    private String message;
    private NetworkRequest request;
    private Map<String, List<String>> headers;

    public NetworkResponse(Builder builder) {
        this.code = builder.code;
        this.body = builder.body;
        this.message = builder.message;
        this.request = builder.request;
        this.headers = builder.headers;
    }

    public int code() {
        return code;
    }

    public byte[] body() {
        return body;
    }

    public String message() {
        return message;
    }

    public NetworkRequest request() {
        return request;
    }

    public Map<String, List<String>> headers() {
        return headers;
    }

    public static class Builder {
        private int code;
        private byte[] body;
        private String message;
        private NetworkRequest request;
        private Map<String, List<String>> headers;

        public Builder(NetworkRequest request) {
            this.request = request;
            this.headers = new HashMap<>();
        }

        public Builder code(int code) {
            this.code = code;
            return this;
        }

        public Builder body(byte[] body) {
            this.body = body;
            return this;
        }

        public Builder message(String message) {
            this.message = message;
            return this;
        }

        public Builder header(String key, String value) {
            if(TextUtils.isEmpty(key) || TextUtils.isEmpty(value)) {
                return this;
            }

            List<String> list = this.headers.get(key);
            if(list == null) {
                list = new ArrayList<>();
                this.headers.put(key, list);
            }

            list.add(value);
            return this;
        }

        public Builder headers(Map<String, List<String>> headers) {
            if(headers != null) {
                this.headers.putAll(headers);
            }
            return this;
        }

        public NetworkResponse build() {
            return new NetworkResponse(this);
        }
    }
}
