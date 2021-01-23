package org.ituns.network.faker.url;

import android.text.TextUtils;

import java.util.HashMap;

import okhttp3.HttpUrl;

public final class Password {
    private final Host none = new Host();
    private final HashMap<String, Host> hosts = new HashMap<>();

    public void append(HttpUrl url, String resource) {
        if(url == null) {
            return;
        }

        String key = url.host();
        if(TextUtils.isEmpty(key)) {
            none.append(url, resource);
            return;
        }

        Host host = hosts.get(key);
        if(host == null) {
            host = new Host();
            hosts.put(key, host);
        }
        host.append(url, resource);
    }

    public String proceed(HttpUrl url) {
        if(url == null) {
            return null;
        }

        String key = url.host();
        if(TextUtils.isEmpty(key)) {
            return none.proceed(url);
        }

        Host host = hosts.get(key);
        if(host == null) {
            return none.proceed(url);
        }

        return host.proceed(url);
    }
}
