package org.ituns.network.faker.entity;

import android.text.TextUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import okhttp3.HttpUrl;

public final class Path {
    private final Query none = new Query();
    private final HashMap<String, Query> querys = new HashMap<>();

    public void append(HttpUrl url, String resource) {
        if(url == null) {
            return;
        }

        String key = buildQuery(url);
        if(TextUtils.isEmpty(key)) {
            none.append(url, resource);
            return;
        }

        Query query = querys.get(key);
        if(query == null) {
            query = new Query();
            querys.put(key, query);
        }
        query.append(url, resource);
    }

    public String proceed(HttpUrl url) {
        if(url == null) {
            return null;
        }

        String key = buildQuery(url);
        if(TextUtils.isEmpty(key)) {
            return none.proceed(url);
        }

        Query query = querys.get(key);
        if(query == null) {
            return none.proceed(url);
        }

        return query.proceed(url);
    }

    private String buildQuery(HttpUrl url) {
        if(url.querySize() == 0) {
            return null;
        } else {
            return translateQuery(url);
        }
    }

    private String translateQuery(HttpUrl url) {
        ArrayList<QueryItem> items = new ArrayList<>();
        for(int i = 0, size = url.querySize(); i < size; i++) {
            String name = url.queryParameterName(i * 2);
            if(TextUtils.isEmpty(name)) {
                name = "";
            }
            String value = url.queryParameterValue(i * 2 + 1);
            if(TextUtils.isEmpty(value)) {
                value = "";
            }
            items.add(new QueryItem(name, value));
        }

        Collections.sort(items, new QueryComparator());

        StringBuilder builder = new StringBuilder();
        for (int i = 0, size = items.size(); i < size; i++) {
            if(i > 0) builder.append('&');
            QueryItem item = items.get(i);
            builder.append(item.name);
            builder.append('=');
            builder.append(item.value);
        }
        return builder.toString();
    }

    private static class QueryItem {
        public String name;
        public String value;

        public QueryItem(String name, String value) {
            this.name = name;
            this.value = value;
        }
    }

    private class QueryComparator implements Comparator<QueryItem> {

        @Override
        public int compare(QueryItem o1, QueryItem o2) {
            int name = o1.name.compareTo(o2.name);
            if(name == 0) {
                return o1.value.compareTo(o2.value);
            } else {
                return name;
            }
        }
    }
}
