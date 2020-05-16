package org.ituns.network.core;

import android.text.TextUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NetworkRequest {
    private String url;
    private byte[] body;
    private NetworkMethod method;
    private Map<String, List<String>> headers;

    private NetworkRequest(Builder builder) {
        url = builder.url;
        body = builder.body;
        method = builder.method;
        headers = builder.headers;
    }

    public String url() {
        return url;
    }

    public byte[] body() {
        return body;
    }

    public NetworkMethod method() {
        return method;
    }

    public Map<String, List<String>> headers() {
        return headers;
    }

    public static class Builder {
        private String url;
        private byte[] body;
        private NetworkMethod method;
        private Map<String, List<String>> headers;

        public Builder(String url) {
            this.url = url;
            headers = new HashMap<>();
            method = NetworkMethod.GET;
        }

        public Builder url(String url) {
            this.url = url;
            return this;
        }

        public Builder body(byte[] body) {
            this.body = body;
            return this;
        }

        public Builder method(NetworkMethod method) {
            this.method = method;
            return this;
        }

        public Builder header(String key, String value) {
            if(TextUtils.isEmpty(key) || value == null) {
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

        public NetworkRequest build() {
            return new NetworkRequest(this);
        }
    }
}
