package org.ituns.network.core.temp;

public enum  Method {
    GET("GET"),
    HEAD("HEAD"),
    POST("POST"),
    DELETE("DELETE"),
    PUT("PUT"),
    PATCH("PATCH");

    private String name;

    Method(String name) {
        this.name = name;
    }
}
