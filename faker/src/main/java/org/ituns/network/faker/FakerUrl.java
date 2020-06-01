package org.ituns.network.faker;

import android.text.TextUtils;

import org.ituns.network.faker.entity.Scheme;

import java.util.HashMap;

import okhttp3.HttpUrl;

class FakerUrl {
    private final HashMap<String, Scheme> schemes = new HashMap<>();

    public void append(String url, String resource) {
        HttpUrl httpUrl = HttpUrl.parse(url);
        if(httpUrl == null) {
            return;
        }

        String key = httpUrl.scheme();
        if(TextUtils.isEmpty(key)) {
            return;
        }

        Scheme scheme = schemes.get(key);
        if(scheme == null) {
            scheme = new Scheme();
            schemes.put(key, scheme);
        }
        scheme.append(httpUrl, resource);
    }

    public String proceed(String url) {
        HttpUrl httpUrl = HttpUrl.parse(url);
        if(httpUrl == null) {
            return null;
        }

        String key = httpUrl.scheme();
        if(TextUtils.isEmpty(key)) {
            return null;
        }

        Scheme scheme = schemes.get(key);
        if(scheme == null) {
            return null;
        }

        return scheme.proceed(httpUrl);
    }
}
