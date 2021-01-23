package org.ituns.network.okhttp.stable;

import org.ituns.network.core.MediaType;
import org.ituns.network.core.Request;

import java.io.File;
import java.io.IOException;
import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class OkHttpBuffer implements Request.Sink {
    private Request.Body body;
    private RequestBody requestBody;

    public OkHttpBuffer(Request.Body body) {
        this.body = body;
    }

    public RequestBody okhttpBody() throws IOException {
        if(body != null) {
            body.writeTo(this);
        }
        return requestBody;
    }

    @Override
    public void write(byte[] bytes) throws IOException {
        if(body != null) {
            requestBody = RequestBody.create(transMediaType(body.mediaType()), bytes);
        }
    }

    @Override
    public void write(File file) throws IOException {
        if(body != null) {
            String type = body.mediaType().name();
            requestBody = RequestBody.create(transMediaType(body.mediaType()), file);
        }
    }

    @Override
    public void write(String content) throws IOException {
        if(body != null) {
            String type = body.mediaType().name();
            requestBody = RequestBody.create(transMediaType(body.mediaType()), content);
        }
    }

    @Override
    public void write(String boundary, List<Request.MultiPart> parts) throws IOException {
        if(body != null) {
            MultipartBody.Builder builder = new MultipartBody.Builder(boundary);
            builder.setType(transMediaType(body.mediaType()));
            for(Request.MultiPart part : parts) {
                OkHttpBuffer buffer = new OkHttpBuffer(part.body());
                builder.addFormDataPart(part.name(), part.filename(), buffer.okhttpBody());
            }
            requestBody = builder.build();
        }
    }

    private okhttp3.MediaType transMediaType(MediaType mediaType) {
        if(mediaType == null) {
            return okhttp3.MediaType.parse("text/plain");
        } else {
            return okhttp3.MediaType.parse(mediaType.name());
        }
    }
}
