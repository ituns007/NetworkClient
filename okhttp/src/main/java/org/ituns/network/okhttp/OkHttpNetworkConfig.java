package org.ituns.network.okhttp;

public interface OkHttpNetworkConfig {
    int TIME_OUT_CONNECT = 30;
    int TIME_OUT_READ = 30;
    int TIME_OUT_WRITE = 30;
    long CACHE_SIZE = 10 * 1024 * 1024;
    String CACHE_DIRECTORY = "cache";
}
