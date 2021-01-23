package org.ituns.network.faker.url;

import okhttp3.HttpUrl;

public final class Fragment {
    private String resource;

    public void append(HttpUrl url, String resource) {
        this.resource = resource;
    }

    public boolean exists(HttpUrl url) {
        return true;
    }

    public String proceed(HttpUrl url) {
        return resource;
    }
}
