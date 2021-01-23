package org.ituns.network.faker.url;

import android.text.TextUtils;

import java.util.HashMap;

import okhttp3.HttpUrl;

public final class Scheme {
    private final Username none = new Username();
    private final HashMap<String, Username> usernames = new HashMap<>();

    public void append(HttpUrl url, String resource) {
        if(url == null) {
            return;
        }

        String key = url.username();
        if(TextUtils.isEmpty(key)) {
            none.append(url, resource);
            return;
        }

        Username username = usernames.get(key);
        if(username == null) {
            username = new Username();
            usernames.put(key, username);
        }
        username.append(url, resource);
    }

    public String proceed(HttpUrl url) {
        if(url == null) {
            return null;
        }

        String key = url.username();
        if(TextUtils.isEmpty(key)) {
            return none.proceed(url);
        }

        Username username = usernames.get(key);
        if(username == null) {
            return none.proceed(url);
        }

        return username.proceed(url);
    }
}
