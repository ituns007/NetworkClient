package org.ituns.network.core;

public enum NetworkMethod {
    GET("GET"),
    POST("POST"),
    HEAD("HEAD"),
    DELETE("DELETE"),
    PUT("PUT"),
    PATCH("PATCH");

    private String text;

    private NetworkMethod(String text) {
        this.text = text;
    }

    public String text() {
        return text;
    }
}
