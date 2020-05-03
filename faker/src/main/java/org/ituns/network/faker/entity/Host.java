package org.ituns.network.faker.entity;

import android.text.TextUtils;

import java.util.HashMap;

import okhttp3.HttpUrl;

public final class Host {
    private final Port none = new Port();
    private final HashMap<String, Port> ports = new HashMap<>();

    public void append(HttpUrl url, String resource) {
        String key = String.valueOf(url.port());
        if(TextUtils.isEmpty(key)) {
            none.append(url, resource);
        } else {
            Port port = ports.get(key);
            if(port == null) {
                port = new Port();
                ports.put(key, port);
            }
            port.append(url, resource);
        }
    }

    public boolean exists(HttpUrl url) {
        String key = String.valueOf(url.port());
        if(TextUtils.isEmpty(key)) {
            return none.exists(url);
        } else {
            Port port = ports.get(key);
            if(port == null) {
                return false;
            } else {
                return port.exists(url);
            }
        }
    }

    public String proceed(HttpUrl url) {
        String key = String.valueOf(url.port());
        if(TextUtils.isEmpty(key)) {
            return none.proceed(url);
        } else {
            Port port = ports.get(key);
            if(port == null) {
                return null;
            } else {
                return port.proceed(url);
            }
        }
    }
}
