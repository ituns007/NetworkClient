package org.ituns.network.okhttp.stable;

import android.content.Context;
import android.os.Environment;

import org.ituns.network.core.Callback;
import org.ituns.network.core.Client;
import org.ituns.network.core.Code;
import org.ituns.network.core.MediaType;
import org.ituns.network.core.Request;
import org.ituns.network.core.Response;

import java.io.File;
import java.io.IOException;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.Call;
import okhttp3.JavaNetCookieJar;
import okhttp3.ResponseBody;

public class OkHttpClient extends Client {
    private okhttp3.OkHttpClient mOkHttpClient;

    public OkHttpClient(OkHttpConfig config) {
        super(config.logger());
        mOkHttpClient = new okhttp3.OkHttpClient.Builder()
                .readTimeout(config.timeOut(), TimeUnit.SECONDS)
                .writeTimeout(config.timeOut(), TimeUnit.SECONDS)
                .connectTimeout(config.timeOut(), TimeUnit.SECONDS)
                .cache(new Cache(cacheDirectory(config.context(), config.cacheDirectory()), config.cacheSize()))
                .cookieJar(new JavaNetCookieJar(new CookieManager(null, CookiePolicy.ACCEPT_ORIGINAL_SERVER)))
                .build();
    }

    private File cacheDirectory(Context context, String directory) {
        String cachePath;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                || !Environment.isExternalStorageRemovable()) {
            cachePath = context.getExternalCacheDir().getPath();
        } else {
            cachePath = context.getCacheDir().getPath();
        }
        return new File(cachePath + File.separator + directory);
    }

    @Override
    protected Response onRequestSync(Request request) {
        if(request == null) {
            return new Response.Builder()
                    .request(request)
                    .code(Code.FAIL_REQ)
                    .message("request is null.")
                    .build();
        }

        okhttp3.Request okhttpRequest = OkHttpRequest.create(request);
        if(okhttpRequest == null) {
            return new Response.Builder()
                    .request(request)
                    .code(Code.FAIL_REQ)
                    .message("okhttp request is null.")
                    .build();
        }

        okhttp3.OkHttpClient okhttpClient = mOkHttpClient;
        if(okhttpClient == null) {
            return new Response.Builder()
                    .request(request)
                    .code(Code.FAIL_REQ)
                    .message("okhttp client is null.")
                    .build();
        }

        try {
            okhttp3.Response response = okhttpClient.newCall(okhttpRequest).execute();
            return OkHttpResponse.okhttp(request, response);
        } catch (IOException e) {
            return new Response.Builder()
                    .request(request)
                    .code(Code.FAIL_RESP)
                    .message("okhttp exception:" + e.getMessage())
                    .build();
        }
    }

    @Override
    protected void onRequestAsync(Request request, Callback callback) {
        if(callback == null) {
            return;
        }

        if(request == null) {
            callback.onResponse(new Response.Builder()
                    .request(request)
                    .code(Code.FAIL_REQ)
                    .message("request is null.")
                    .build());
        }

        okhttp3.Request okhttpRequest = OkHttpRequest.create(request);
        if(okhttpRequest == null) {
            callback.onResponse(new Response.Builder()
                    .request(request)
                    .code(Code.FAIL_REQ)
                    .message("okhttp request is null.")
                    .build());
        }

        okhttp3.OkHttpClient okhttpClient = mOkHttpClient;
        if(okhttpClient == null) {
            callback.onResponse(new Response.Builder()
                    .request(request)
                    .code(Code.FAIL_REQ)
                    .message("okhttp client is null.")
                    .build());
        }

        okhttpClient.newCall(okhttpRequest).enqueue(new OkhttpCallback(request, callback));
    }

    @Override
    public void release() {}

    private static class OkhttpCallback implements okhttp3.Callback {
        private Request mRequest;
        private Callback mCallback;

        public OkhttpCallback(Request request, Callback callback) {
            mRequest = request;
            mCallback = callback;
        }

        @Override
        public void onFailure(Call call, IOException e) {
            Callback callback = mCallback;
            if(callback == null) {
                release();
                return;
            }

            callback.onResponse(new Response.Builder()
                    .request(mRequest)
                    .code(Code.FAIL_RESP)
                    .message("okhttp exception:" + e.getMessage())
                    .build());
        }

        @Override
        public void onResponse(Call call, okhttp3.Response response) throws IOException {
            Callback callback = mCallback;
            if(callback == null) {
                release();
                return;
            }

            if(response == null) {
                callback.onResponse(new Response.Builder()
                        .request(mRequest)
                        .code(Code.FAIL_RESP)
                        .message("okhttp response is null.")
                        .build());
                release();
            }

            Response.Body body = null;
            ResponseBody responseBody = response.body();
            if(responseBody  != null) {
                body = Response.Body.create(MediaType.parse(responseBody.contentType().toString()),
                        responseBody.bytes());
            }

            callback.onResponse(new Response.Builder()
                    .request(mRequest)
                    .code(response.code())
                    .message(response.message())
                    .headers(response.headers().toMultimap())
                    .body(body)
                    .build());
            release();
        }

        private void release() {
            mRequest = null;
            mCallback = null;
        }
    }
}
