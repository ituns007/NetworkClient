package org.ituns.network.faker.url;

import android.text.TextUtils;

import java.util.HashMap;

import okhttp3.HttpUrl;

public final class Query {
    private final Fragment none = new Fragment();
    private final HashMap<String, Fragment> fragments = new HashMap<>();

    public void append(HttpUrl url, String resource) {
        if(url == null) {
            return;
        }

        String key = url.fragment();
        if(TextUtils.isEmpty(key)) {
            none.append(url, resource);
            return;
        }

        Fragment fragment = fragments.get(key);
        if(fragment == null) {
            fragment = new Fragment();
            fragments.put(key, fragment);
        }
        fragment.append(url, resource);
    }

    public String proceed(HttpUrl url) {
        if(url == null) {
            return null;
        }

        String key = url.fragment();
        if(TextUtils.isEmpty(key)) {
            return none.proceed(url);
        }

        Fragment fragment = fragments.get(key);
        if(fragment == null) {
            return none.proceed(url);
        }

        return fragment.proceed(url);
    }
}
