package org.ituns.network.okhttp.compat;

import android.content.Context;

import org.ituns.network.core.Config;
import org.ituns.network.core.Logger;

public class OkHttpConfig extends Config {
    private static final long DEFAULT_TIME_OUT = 30;
    private static final long DEFAULT_CACHE_SIZE = 10 * 1024 * 1024;
    private static final String DEFAULT_CACHE_DIRECTORY = "okhttp";

    private Context context;
    private long timeOut;
    private long cacheSize;
    private String cacheDirectory;

    private OkHttpConfig(Builder builder) {
        super(builder.logger);
        this.context = builder.context;
        this.timeOut = builder.timeOut;
        this.cacheSize = builder.cacheSize;
        this.cacheDirectory = builder.cacheDirectory;
    }

    public Context context() {
        return context;
    }

    public long timeOut() {
        return timeOut;
    }

    public long cacheSize() {
        return cacheSize;
    }

    public String cacheDirectory() {
        return cacheDirectory;
    }

    public static class Builder {
        private Context context;
        private Logger logger;
        private long timeOut;
        private long cacheSize;
        private String cacheDirectory;

        public Builder(Context context) {
            this.context = context;
            this.timeOut = DEFAULT_TIME_OUT;
            this.cacheSize = DEFAULT_CACHE_SIZE;
            this.cacheDirectory = DEFAULT_CACHE_DIRECTORY;
        }

        public Builder logger(Logger logger) {
            this.logger = logger;
            return this;
        }

        public Builder timeOut(long timeOut) {
            this.timeOut = timeOut;
            return this;
        }

        public Builder cacheSize(long cacheSize) {
            this.cacheSize = cacheSize;
            return this;
        }

        public Builder cacheDirectory(String cacheDirectory) {
            this.cacheDirectory = cacheDirectory;
            return this;
        }

        public OkHttpConfig build() {
            return new OkHttpConfig(this);
        }
    }
}
