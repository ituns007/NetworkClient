package org.ituns.network.faker.entity;

import android.text.TextUtils;

import java.util.HashMap;

import okhttp3.HttpUrl;

public final class Scheme {
    private final Username none = new Username();
    private final HashMap<String, Username> usernames = new HashMap<>();

    public void append(HttpUrl url, String resource) {
        String key = url.username();
        if(TextUtils.isEmpty(key)) {
            none.append(url, resource);
        } else {
            Username username = usernames.get(key);
            if(username == null) {
                username = new Username();
                usernames.put(key, username);
            }
            username.append(url, resource);
        }
    }

    public boolean exists(HttpUrl url) {
        String key = url.username();
        if(TextUtils.isEmpty(key)) {
            return none.exists(url);
        } else {
            Username username = usernames.get(key);
            if(username == null) {
                return false;
            } else {
                return username.exists(url);
            }
        }
    }

    public String proceed(HttpUrl url) {
        String key = url.username();
        if(TextUtils.isEmpty(key)) {
            return none.proceed(url);
        } else {
            Username username = usernames.get(key);
            if(username == null) {
                return null;
            } else {
                return username.proceed(url);
            }
        }
    }
}
