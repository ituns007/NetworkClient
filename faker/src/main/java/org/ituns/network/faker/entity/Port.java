package org.ituns.network.faker.entity;

import android.text.TextUtils;

import java.util.HashMap;
import java.util.List;

import okhttp3.HttpUrl;

public final class Port {
    private final Path none = new Path();
    private final HashMap<String, Path> paths = new HashMap<>();

    public void append(HttpUrl url, String string) {
        if(url == null) {
            return;
        }

        String key = buildPath(url);
        if(TextUtils.isEmpty(key)) {
            none.append(url, string);
            return;
        }

        Path path = paths.get(key);
        if(path == null) {
            path = new Path();
            paths.put(key, path);
        }
        path.append(url, string);
    }

    public String proceed(HttpUrl url) {
        if(url == null) {
            return null;
        }

        String key = buildPath(url);
        if(TextUtils.isEmpty(key)) {
            return none.proceed(url);
        }

        Path path = paths.get(key);
        if(path == null) {
            return none.proceed(url);
        }

        return path.proceed(url);
    }

    private String buildPath(HttpUrl url) {
        StringBuilder builder = new StringBuilder();
        List<String> list = url.pathSegments();
        for(int i = 0, size = list.size(); i < list.size(); i++) {
            builder.append('/');
            builder.append(list.get(i));
        }
        return builder.toString();
    }
}
