package org.ituns.network.faker.entity;


import android.text.TextUtils;

import java.util.HashMap;

import okhttp3.HttpUrl;

public final class Password {
    private final Host none = new Host();
    private final HashMap<String, Host> hosts = new HashMap<>();

    public void append(HttpUrl url, String resource) {
        String key = url.host();
        if(TextUtils.isEmpty(key)) {
            none.append(url, resource);
        } else {
            Host host = hosts.get(key);
            if(host == null) {
                host = new Host();
                hosts.put(key, host);
            }
            host.append(url, resource);
        }
    }

    public boolean exists(HttpUrl url) {
        String key = url.host();
        if(TextUtils.isEmpty(key)) {
            return none.exists(url);
        } else {
            Host host = hosts.get(key);
            if(host == null) {
                return false;
            } else {
                return host.exists(url);
            }
        }
    }

    public String proceed(HttpUrl url) {
        String key = url.host();
        if(TextUtils.isEmpty(key)) {
            return none.proceed(url);
        } else {
            Host host = hosts.get(key);
            if(host == null) {
                return null;
            } else {
                return host.proceed(url);
            }
        }
    }
}
