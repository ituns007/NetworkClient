package org.ituns.network.core;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.List;

public final class Buffer extends Response.Source implements Request.Sink {
    private static final byte[] CRLF = {'\r', '\n'};
    private static final byte[] DASHDASH = {'-', '-'};

    private final ByteArrayOutputStream baos = new ByteArrayOutputStream();

    @Override
    public void write(byte[] bytes) throws IOException {
        baos.write(bytes);
    }

    @Override
    public void write(File file) throws IOException {
        int length = 0;
        byte[] buffer = new byte[4096];
        FileInputStream fis = new FileInputStream(file);
        while ((length = fis.read(buffer)) != -1) {
            baos.write(buffer, 0, length);
        }
        fis.close();
    }

    @Override
    public void write(String content) throws IOException {
        baos.write(content.getBytes());
    }

    @Override
    public void write(String boundary, List<Request.MultiPart> parts) throws IOException {
        for(Request.MultiPart part : parts) {
            part.writeTo(this, boundary);
        }
        this.write(DASHDASH);
        this.write(boundary);
        this.write(DASHDASH);
        this.write(CRLF);
    }

    @Override
    public byte[] readBytes() throws IOException {
        return baos.toByteArray();
    }

    @Override
    public String readUtf8() throws IOException {
        return new String(baos.toByteArray());
    }

    @Override
    public String readString(Charset charset) throws IOException {
        return new String(baos.toByteArray(), charset);
    }

    @Override
    public InputStream stream() {
        return new ByteArrayInputStream(baos.toByteArray());
    }
}
