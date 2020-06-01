package org.ituns.network.faker.entity;

import android.text.TextUtils;

import java.util.HashMap;

import okhttp3.HttpUrl;

public final class Username {
    private final Password none = new Password();
    private final HashMap<String, Password> passwords = new HashMap<>();

    public void append(HttpUrl url, String content) {
        if(url == null) {
            return;
        }

        String key = url.password();
        if(TextUtils.isEmpty(key)) {
            none.append(url, content);
            return;
        }

        Password password = passwords.get(key);
        if(password == null) {
            password = new Password();
            passwords.put(key, password);
        }
        password.append(url, content);
    }

    public String proceed(HttpUrl url) {
        if(url == null) {
            return null;
        }

        String key = url.password();
        if(TextUtils.isEmpty(key)) {
            return none.proceed(url);
        }

        Password password = passwords.get(key);
        if(password == null) {
            return none.proceed(url);
        }

        return password.proceed(url);
    }
}
