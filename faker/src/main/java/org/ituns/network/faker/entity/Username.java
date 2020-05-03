package org.ituns.network.faker.entity;

import android.text.TextUtils;

import java.util.HashMap;

import okhttp3.HttpUrl;

public final class Username {
    private final Password none = new Password();
    private final HashMap<String, Password> passwords = new HashMap<>();

    public void append(HttpUrl url, String content) {
        String key = url.password();
        if(TextUtils.isEmpty(key)) {
            none.append(url, content);
        } else {
            Password password = passwords.get(key);
            if(password == null) {
                password = new Password();
                passwords.put(key, password);
            }
            password.append(url, content);
        }
    }

    public boolean exists(HttpUrl url) {
        String key = url.password();
        if(TextUtils.isEmpty(key)) {
            return none.exists(url);
        } else {
            Password password = passwords.get(key);
            if(password == null) {
                return false;
            } else {
                return password.exists(url);
            }
        }
    }

    public String proceed(HttpUrl url) {
        String key = url.password();
        if(TextUtils.isEmpty(key)) {
            return none.proceed(url);
        } else {
            Password password = passwords.get(key);
            if(password == null) {
                return null;
            } else {
                return password.proceed(url);
            }
        }
    }
}
