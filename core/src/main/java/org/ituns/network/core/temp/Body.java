package org.ituns.network.core.temp;

import java.io.File;

public abstract class Body {

    public abstract MediaType mediaType();

    public abstract void writeTo(Sink sink);

    public static Body create(final MediaType type, final byte[] bytes) {
        return new Body() {
            @Override
            public MediaType mediaType() {
                return type;
            }

            @Override
            public void writeTo(Sink sink) {
                sink.write(bytes);
            }
        };
    }

    public static Body create(final MediaType type, final File file) {
        return new Body() {
            @Override
            public MediaType mediaType() {
                return type;
            }

            @Override
            public void writeTo(Sink sink) {
                sink.write(file);
            }
        };
    }

    public static Body create(final MediaType type, final String content) {
        return new Body() {
            @Override
            public MediaType mediaType() {
                return type;
            }

            @Override
            public void writeTo(Sink sink) {
                sink.write(content);
            }
        };
    }
}
