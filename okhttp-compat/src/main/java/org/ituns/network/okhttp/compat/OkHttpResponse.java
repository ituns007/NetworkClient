package org.ituns.network.okhttp.compat;

import android.net.NetworkRequest;

import org.ituns.network.core.Code;
import org.ituns.network.core.MediaType;
import org.ituns.network.core.Request;
import org.ituns.network.core.Response;

import java.io.IOException;

import okhttp3.ResponseBody;

public class OkHttpResponse {

    public static Response okhttp(Request request, okhttp3.Response response) throws IOException {
        if(response == null) {
            return new Response.Builder()
                    .request(request)
                    .code(Code.FAIL_RESP)
                    .message("okhttp response is null.")
                    .build();
        }

        Response.Body body = null;
        ResponseBody responseBody = response.body();
        if(responseBody  != null) {
            body = Response.Body.create(MediaType.parse(responseBody.contentType().toString()),
                    responseBody.bytes());
        }

        return new Response.Builder()
                .request(request)
                .code(response.code())
                .message(response.message())
                .headers(response.headers().toMultimap())
                .body(body)
                .build();
    }
}
