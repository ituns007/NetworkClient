package org.ituns.network.core.temp;

import org.ituns.network.core.body.MultiBody;

import java.io.File;

public interface Sink {

    void write(byte[] bytes);

    void write(File file);

    void write(String content);

    void write(MultiBody body);
}
