package org.ituns.network.faker.url;

import android.text.TextUtils;

import java.util.HashMap;

import okhttp3.HttpUrl;

public final class Host {
    private final Port none = new Port();
    private final HashMap<String, Port> ports = new HashMap<>();

    public void append(HttpUrl url, String resource) {
        if(url == null) {
            return;
        }

        String key = String.valueOf(url.port());
        if(TextUtils.isEmpty(key)) {
            none.append(url, resource);
            return;
        }

        Port port = ports.get(key);
        if(port == null) {
            port = new Port();
            ports.put(key, port);
        }
        port.append(url, resource);
    }

    public String proceed(HttpUrl url) {
        if(url == null) {
            return null;
        }

        String key = String.valueOf(url.port());
        if(TextUtils.isEmpty(key)) {
            return none.proceed(url);
        }

        Port port = ports.get(key);
        if(port == null) {
            return none.proceed(url);
        }

        return port.proceed(url);
    }
}
