package org.ituns.network.core;

public abstract class Config {
    private Logger logger;

    public Config(Logger logger) {
        this.logger = logger;
    }

    public Logger logger() {
        return logger;
    }
}
