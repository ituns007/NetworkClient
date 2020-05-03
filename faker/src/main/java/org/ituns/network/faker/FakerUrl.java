package org.ituns.network.faker;

import android.text.TextUtils;

import org.ituns.network.faker.entity.Scheme;

import java.util.HashMap;

import okhttp3.HttpUrl;

public class FakerUrl {
    private final Scheme none = new Scheme();
    private final HashMap<String, Scheme> schemes = new HashMap<>();

    public void append(String url, String resource) {
        HttpUrl httpUrl = HttpUrl.parse(url);
        if(httpUrl == null) {
            return;
        }

        String key = httpUrl.scheme();
        if(TextUtils.isEmpty(key)) {
            none.append(httpUrl, resource);
        } else {
            Scheme scheme = schemes.get(key);
            if(scheme == null) {
                scheme = new Scheme();
                schemes.put(key, scheme);
            }
            scheme.append(httpUrl, resource);
        }
    }

    public boolean exists(String url) {
        HttpUrl httpUrl = HttpUrl.parse(url);
        if(httpUrl == null) {
            return false;
        }

        String key = httpUrl.scheme();
        if(TextUtils.isEmpty(key)) {
            return none.exists(httpUrl);
        } else {
            Scheme scheme = schemes.get(key);
            if(scheme == null) {
                return false;
            } else {
                return scheme.exists(httpUrl);
            }
        }
    }

    public String proceed(String url) {
        HttpUrl httpUrl = HttpUrl.parse(url);
        if(httpUrl == null) {
            return null;
        }

        String key = httpUrl.scheme();
        if(TextUtils.isEmpty(key)) {
            return none.proceed(httpUrl);
        } else {
            Scheme scheme = schemes.get(key);
            if(scheme == null) {
                return null;
            } else {
                return scheme.proceed(httpUrl);
            }
        }
    }
}
