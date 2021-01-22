package org.ituns.network.core;

public interface Logger {
    void log(String msg);
    void log(Throwable t);
}
