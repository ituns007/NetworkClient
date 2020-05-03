package org.ituns.network.faker.entity;

import android.text.TextUtils;

import java.util.HashMap;

import okhttp3.HttpUrl;

public final class Query {
    private final Fragment none = new Fragment();
    private final HashMap<String, Fragment> fragments = new HashMap<>();

    public void append(HttpUrl url, String resource) {
        String key = url.fragment();
        if(TextUtils.isEmpty(key)) {
            none.append(url, resource);
        } else {
            Fragment fragment = fragments.get(key);
            if(fragment == null) {
                fragment = new Fragment();
                fragments.put(key, fragment);
            }
            fragment.append(url, resource);
        }
    }

    public boolean exists(HttpUrl url) {
        String key = url.fragment();
        if(TextUtils.isEmpty(key)) {
            return none.exists(url);
        } else {
            Fragment fragment = fragments.get(key);
            if(fragment == null) {
                return false;
            } else {
                return fragment.exists(url);
            }
        }
    }

    public String proceed(HttpUrl url) {
        String key = url.fragment();
        if(TextUtils.isEmpty(key)) {
            return none.proceed(url);
        } else {
            Fragment fragment = fragments.get(key);
            if(fragment == null) {
                return null;
            } else {
                return fragment.proceed(url);
            }
        }
    }
}
