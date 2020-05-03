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
        String key = buildQuery(url);
        if(TextUtils.isEmpty(key)) {
            none.append(url, resource);
        } else {
            Query query = querys.get(key);
            if(query == null) {
                query = new Query();
                querys.put(key, query);
            }
            query.append(url, resource);
        }
    }

    public boolean exists(HttpUrl url) {
        String key = buildQuery(url);
        if(TextUtils.isEmpty(key)) {
            return none.exists(url);
        } else {
            Query query = querys.get(key);
            if(query == null) {
                return false;
            } else {
                return query.exists(url);
            }
        }
    }

    public String proceed(HttpUrl url) {
        String key = buildQuery(url);
        if(TextUtils.isEmpty(key)) {
            return none.proceed(url);
        } else {
            Query query = querys.get(key);
            if(query == null) {
                return null;
            } else {
                return query.proceed(url);
            }
        }
    }

    private String buildQuery(HttpUrl url) {
        if(url.querySize() == 0) {
            return null;
        } else {
            return translateQuery(url);
        }
    }

    private String translateQuery(HttpUrl url) {
        ArrayList<Item> items = new ArrayList<>();
        for(int i = 0, size = url.querySize(); i < size; i++) {
            String name = url.queryParameterName(i * 2);
            if(TextUtils.isEmpty(name)) {
                name = "";
            }
            String value = url.queryParameterValue(i * 2 + 1);
            if(TextUtils.isEmpty(value)) {
                value = "";
            }
            items.add(new Item(name, value));
        }

        Collections.sort(items, new ItemComparator());

        StringBuilder builder = new StringBuilder();
        for (int i = 0, size = items.size(); i < size; i++) {
            if(i > 0) builder.append('&');
            Item item = items.get(i);
            builder.append(item.name);
            builder.append('=');
            builder.append(item.value);
        }
        return builder.toString();
    }

    private class Item {
        public String name;
        public String value;

        public Item(String name, String value) {
            this.name = name;
            this.value = value;
        }
    }

    private class ItemComparator implements Comparator<Item> {

        @Override
        public int compare(Item o1, Item o2) {
            int name = o1.name.compareTo(o2.name);
            if(name == 0) {
                return o1.value.compareTo(o2.value);
            } else {
                return name;
            }
        }
    }
}
