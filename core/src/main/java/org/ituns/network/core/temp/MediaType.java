package org.ituns.network.core.temp;

public class MediaType {
    private String type;

    private MediaType(String type) {
        this.type = type;
    }

    public String type() {
        return type;
    }

    public static MediaType parse(String type) {
        return new MediaType(type);
    }
}