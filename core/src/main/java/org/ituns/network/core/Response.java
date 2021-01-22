package org.ituns.network.core;

import android.text.TextUtils;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class Response {
    private Request request;
    private int code;
    private String message;
    private Map<String, List<String>> headers;
    private Body body;

    private Response(Builder builder) {
        this.request = builder.request;
        this.code = builder.code;
        this.message = builder.message;
        this.headers = builder.headers;
        this.body = builder.body;
    }

    public Request request() {
        return request;
    }

    public int code() {
        return code;
    }

    public String message() {
        return message;
    }

    public String header(String name) {
        return header(name, null);
    }

    public String header(String name, String defaultValue) {
        if(headers != null) {
            List<String> list = headers.get(name);
            if(list != null && list.size() > 0) {
                return list.get(list.size() - 1);
            }
        }
        return defaultValue;
    }

    public List<String> headers(String name) {
        if(headers != null) {
            return headers.get(name);
        }
        return null;
    }

    public Map<String, List<String>> headers() {
        return headers;
    }

    public Body body() {
        return body;
    }

    public static abstract class Source {
        public abstract byte[] readBytes() throws IOException;

        public abstract String readUtf8() throws IOException;

        public abstract String readString(Charset charset) throws IOException;

        public abstract InputStream stream();

        public static Source create(final byte[] content) {
            return new Source() {
                @Override
                public byte[] readBytes() throws IOException {
                    return content;
                }

                @Override
                public String readUtf8() throws IOException {
                    return new String(content, "UTF-8");
                }

                @Override
                public String readString(Charset charset) throws IOException {
                    return new String(content, charset);
                }

                @Override
                public InputStream stream() {
                    return new ByteArrayInputStream(content);
                }
            };
        }

        public static Source create(final String content) {
            return new Source() {
                @Override
                public byte[] readBytes() throws IOException {
                    return content.getBytes();
                }

                @Override
                public String readUtf8() throws IOException {
                    return content;
                }

                @Override
                public String readString(Charset charset) throws IOException {
                    return content;
                }

                @Override
                public InputStream stream() {
                    return new ByteArrayInputStream(content.getBytes());
                }
            };
        }

        public static Source create(final InputStream stream) {
            return new Source() {
                @Override
                public byte[] readBytes() throws IOException {
                    return readFromStream(stream);
                }

                @Override
                public String readUtf8() throws IOException {
                    byte[] bytes = readFromStream(stream);
                    return new String(bytes, "UTF-8");
                }

                @Override
                public String readString(Charset charset) throws IOException {
                    byte[] bytes = readFromStream(stream);
                    return new String(bytes, charset);
                }

                @Override
                public InputStream stream() {
                    return new BufferedInputStream(stream);
                }

                private byte[] readFromStream(InputStream stream) throws IOException {
                    int length = 0;
                    byte[] buffer = new byte[4096];
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    while ((length = stream.read(buffer)) != -1) {
                        baos.write(buffer, 0, length);
                    }
                    stream.close();
                    return baos.toByteArray();
                }
            };
        }
    }

    public static abstract class Body {
        public abstract MediaType type();

        public abstract long length();

        public abstract Source source();

        public final byte[] bytes() throws IOException {
            return source().readBytes();
        }

        public final String string() throws IOException {
            return source().readUtf8();
        }

        public final InputStream stream() throws IOException {
            return source().stream();
        }

        public static Body create(final MediaType type, final byte[] content) {
            return new Body() {
                @Override
                public MediaType type() {
                    return type;
                }

                @Override
                public long length() {
                    return content.length;
                }

                @Override
                public Source source() {
                    return Source.create(content);
                }
            };
        }

        public static Body create(final MediaType type, final String content) {
            return new Body() {
                @Override
                public MediaType type() {
                    return type;
                }

                @Override
                public long length() {
                    return content.length();
                }

                @Override
                public Source source() {
                    return Source.create(content);
                }
            };
        }

        public static Body create(final MediaType type, final long length, final InputStream stream) {
            return new Body() {
                @Override
                public MediaType type() {
                    return type;
                }

                @Override
                public long length() {
                    return length;
                }

                @Override
                public Source source() {
                    return Source.create(stream);
                }
            };
        }
    }

    public static class Builder {
        private Request request;
        private int code;
        private String message;
        private Map<String, List<String>> headers;
        private Body body;

        public Builder() {
            headers = new HashMap<>();
        }

        public Builder request(Request request) {
            this.request = request;
            return this;
        }

        public Builder code(int code) {
            this.code = code;
            return this;
        }

        public Builder message(String message) {
            this.message = message;
            return this;
        }

        public Builder addHeader(String name, String value) {
            if(!TextUtils.isEmpty(name) && !TextUtils.isEmpty(value)) {
                List<String> values = this.headers.get(name);
                if(values == null) {
                    values = new ArrayList<>();
                    this.headers.put(name, values);
                }
                values.add(value);
            }
            return this;
        }

        public Builder setHeader(String name, String value) {
            if(!TextUtils.isEmpty(name) && !TextUtils.isEmpty(value)) {
                this.headers.remove(name);
                ArrayList<String> values = new ArrayList<>();
                values.add(value);
                this.headers.put(name, values);
            }
            return this;
        }

        public Builder headers(Map<String, List<String>> headers) {
            if(headers != null) {
                this.headers.putAll(headers);
            }
            return this;
        }

        public Builder body(Body body) {
            this.body = body;
            return this;
        }

        public Response build() {
            return new Response(this);
        }
    }
}