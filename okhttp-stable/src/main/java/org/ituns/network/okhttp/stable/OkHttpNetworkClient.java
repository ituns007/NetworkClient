package org.ituns.network.okhttp.stable;

import android.util.Log;

import org.ituns.network.core.NetworkCallback;
import org.ituns.network.core.NetworkClient;
import org.ituns.network.core.NetworkCode;
import org.ituns.network.core.NetworkRequest;
import org.ituns.network.core.NetworkResponse;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.JavaNetCookieJar;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class OkHttpNetworkClient extends NetworkClient {
    private static final String TAG = "OkHttpNetworkClient";

    private volatile static OkHttpNetworkClient mInstance;

    private OkHttpClient mOkHttpClient;

    public static OkHttpNetworkClient getInstance() {
        if(mInstance == null) {
            synchronized (OkHttpNetworkClient.class) {
                if(mInstance == null) {
                    mInstance = new OkHttpNetworkClient();
                }
            }
        }
        return mInstance;
    }

    private OkHttpNetworkClient() {
        mOkHttpClient = new OkHttpClient.Builder()
                .readTimeout(OkHttpNetworkConfig.TIME_OUT_READ, TimeUnit.SECONDS)
                .writeTimeout(OkHttpNetworkConfig.TIME_OUT_WRITE, TimeUnit.SECONDS)
                .connectTimeout(OkHttpNetworkConfig.TIME_OUT_CONNECT, TimeUnit.SECONDS)
                .cache(new Cache(new File(OkHttpNetworkConfig.CACHE_DIRECTORY), OkHttpNetworkConfig.CACHE_SIZE))
                .cookieJar(new JavaNetCookieJar(new CookieManager(null, CookiePolicy.ACCEPT_ORIGINAL_SERVER)))
                .build();
    }

    @Override
    protected NetworkResponse onRequestSync(NetworkRequest networkRequest) {
        if(networkRequest == null) {
            logcat(isDebugMode,"network request is null.");
            return new NetworkResponse.Builder(networkRequest)
                    .code(NetworkCode.ERROR_REQUEST)
                    .message("network request is null.")
                    .build();
        }

        Request request = OkHttpNetworkRequest.create(networkRequest).okhttp();
        if(request == null) {
            logcat(isDebugMode,"okhttp request is null.");
            return new NetworkResponse.Builder(networkRequest)
                    .code(NetworkCode.ERROR_REQUEST)
                    .message("okhttp request is null.")
                    .build();
        }

        OkHttpClient okHttpClient = mOkHttpClient;
        if(okHttpClient == null) {
            logcat(isDebugMode,"okhttp client is null.");
            return new NetworkResponse.Builder(networkRequest)
                    .code(NetworkCode.ERROR_REQUEST)
                    .message("okhttp client is null.")
                    .build();
        }

        try {
            Response response = okHttpClient.newCall(request).execute();
            return OkHttpNetworkResponse.create(networkRequest).okhttp(response);
        } catch (IOException e) {
            logcat(isDebugMode,"network exception:" + e.getMessage());
            return new NetworkResponse.Builder(networkRequest)
                    .code(NetworkCode.ERROR_NETWORK)
                    .message("network exception:" + e.getMessage())
                    .build();
        }
    }

    @Override
    protected void onRequestAsync(NetworkRequest networkRequest, NetworkCallback callback) {
        if(callback == null) {
            logcat(isDebugMode,"network callback is null.");
            return;
        }

        if(networkRequest == null) {
            logcat(isDebugMode,"network request is null.");
            callback.onError(new NetworkResponse.Builder(networkRequest)
                    .code(NetworkCode.ERROR_REQUEST)
                    .message("network request is null.")
                    .build());
            return;
        }

        Request request = OkHttpNetworkRequest.create(networkRequest).okhttp();
        if(request == null) {
            logcat(isDebugMode,"okhttp request is null.");
            callback.onError(new NetworkResponse.Builder(networkRequest)
                    .code(NetworkCode.ERROR_REQUEST)
                    .message("okhttp request is null.")
                    .build());
            return;
        }

        OkHttpClient okHttpClient = mOkHttpClient;
        if(okHttpClient == null) {
            logcat(isDebugMode,"okhttp client is null.");
            callback.onError(new NetworkResponse.Builder(networkRequest)
                    .code(NetworkCode.ERROR_REQUEST)
                    .message("okhttp client is null.")
                    .build());
            return;
        }

        okHttpClient.newCall(request).enqueue(new OkHttpCallbackImpl(
                isDebugMode, networkRequest, callback));
    }

    private static class OkHttpCallbackImpl implements Callback {
        private boolean isDebugMode;
        private NetworkRequest mNetworkRequest;
        private NetworkCallback mNetworkCallback;

        public OkHttpCallbackImpl(boolean debugMode, NetworkRequest networkRequest, NetworkCallback networkCallback) {
            isDebugMode = debugMode;
            mNetworkRequest = networkRequest;
            mNetworkCallback = networkCallback;
        }

        @Override
        public void onFailure(@NotNull Call call, @NotNull IOException e) {
            logcat(isDebugMode, "network error:" + e.getMessage());

            NetworkRequest networkRequest = mNetworkRequest;
            NetworkCallback networkCallback = mNetworkCallback;
            if(networkCallback == null) {
                return;
            }

            networkCallback.onError(new NetworkResponse.Builder(networkRequest)
                    .code(NetworkCode.ERROR_NETWORK)
                    .message("network error:" + e.getMessage())
                    .build());
            mNetworkCallback = null;
        }

        @Override
        public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
            NetworkRequest networkRequest = mNetworkRequest;
            NetworkCallback networkCallback = mNetworkCallback;
            if(networkCallback == null) {
                return;
            }

            if(response == null) {
                logcat(isDebugMode,"okhttp response is null.");
                networkCallback.onError(new NetworkResponse.Builder(networkRequest)
                        .code(NetworkCode.ERROR_RESPONSE)
                        .message("okhttp response is null.")
                        .build());
                mNetworkCallback = null;
                return;
            }

            NetworkResponse networkResponse = OkHttpNetworkResponse
                    .create(networkRequest).okhttp(response);
            networkCallback.onSuccess(networkResponse);
            mNetworkCallback = null;
            response.close();
        }
    }

    private static void logcat(boolean debugMode, String msg) {
        if(debugMode) {
            Log.d(OkHttpNetworkClient.TAG, msg);
        }
    }
}
