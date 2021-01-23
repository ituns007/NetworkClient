package org.ituns.network.core;

public class MediaType {
    private String name;

    private MediaType(String name) {
        this.name = name;
    }

    public String name() {
        return name;
    }

    public static MediaType parse(String type) {
        return new MediaType(type);
    }
}