package org.ituns.network.faker;

import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;

import org.ituns.network.core.NetworkCallback;
import org.ituns.network.core.NetworkClient;
import org.ituns.network.core.NetworkCode;
import org.ituns.network.core.NetworkRequest;
import org.ituns.network.core.NetworkResponse;

public final class FakerNetworkClient extends NetworkClient {
    private static final String TAG = "FakerNetworkClient";

    private Handler mBackHandler;
    private HandlerThread mHandlerThread;

    private FakerAdapter mFakerAdapter;

    public FakerNetworkClient(FakerAdapter fakeAdapter) {
        mHandlerThread = new HandlerThread("FakerNetworkClient");
        mHandlerThread.start();
        mBackHandler = new Handler(mHandlerThread.getLooper());
        mFakerAdapter = fakeAdapter;
    }

    public void release() {
        HandlerThread handlerThread = mHandlerThread;
        if(handlerThread != null) {
            handlerThread.quit();
            mHandlerThread = null;
        }

        Handler handler = mBackHandler;
        if(handler != null) {
            mBackHandler = null;
        }

        FakerAdapter fakerAdapter = mFakerAdapter;
        if(fakerAdapter != null) {
            mFakerAdapter = null;
        }
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

        FakerAdapter fakerAdapter = mFakerAdapter;
        if(fakerAdapter == null) {
            logcat(isDebugMode,"faker adapter is null.");
            return new NetworkResponse.Builder(networkRequest)
                    .code(NetworkCode.ERROR_REQUEST)
                    .message("faker adapter is null.")
                    .build();
        }

        return fakerAdapter.readNetworkResponse(networkRequest);
    }

    @Override
    protected void onRequestAsync(NetworkRequest networkRequest, NetworkCallback callback) {
        if(networkRequest == null) {
            logcat(isDebugMode,"network request is null.");
            callback.onError(new NetworkResponse.Builder(networkRequest)
                    .code(NetworkCode.ERROR_REQUEST)
                    .message("network request is null.")
                    .build());
            return;
        }

        FakerAdapter fakerAdapter = mFakerAdapter;
        if(fakerAdapter == null) {
            logcat(isDebugMode,"faker adapter is null.");
            callback.onError(new NetworkResponse.Builder(networkRequest)
                    .code(NetworkCode.ERROR_REQUEST)
                    .message("faker adapter is null.")
                    .build());
            return;
        }

        mBackHandler.post(new RequestAsyncTask(isDebugMode, fakerAdapter,
                networkRequest, callback));
    }

    private static class RequestAsyncTask implements Runnable {
        private boolean isDebugMode;
        private FakerAdapter mFakerAdapter;
        private NetworkRequest mNetworkRequest;
        private NetworkCallback mNetworkCallback;

        public RequestAsyncTask(boolean isDebugMode, FakerAdapter fakerAdapter, NetworkRequest networkRequest, NetworkCallback networkCallback) {
            this.isDebugMode = isDebugMode;
            mFakerAdapter = fakerAdapter;
            mNetworkRequest = networkRequest;
            mNetworkCallback = networkCallback;
        }

        @Override
        public void run() {
            NetworkRequest networkRequest = mNetworkRequest;
            NetworkCallback networkCallback = mNetworkCallback;
            if(networkCallback == null) {
                releaseAsyncTask();
                return;
            }

            FakerAdapter fakerAdapter = mFakerAdapter;
            if(fakerAdapter == null) {
                logcat(isDebugMode,"faker adapter is null.");
                networkCallback.onError(new NetworkResponse.Builder(networkRequest)
                        .code(NetworkCode.ERROR_REQUEST)
                        .message("faker adapter is null.")
                        .build());
                releaseAsyncTask();
                return;
            }

            NetworkResponse networkResponse = fakerAdapter.readNetworkResponse(networkRequest);
            networkCallback.onSuccess(networkResponse);
            releaseAsyncTask();
        }

        private void releaseAsyncTask() {
            mFakerAdapter = null;
            mNetworkRequest = null;
            mNetworkCallback = null;
        }
    }

    private static void logcat(boolean debugMode, String msg) {
        if(debugMode) {
            Log.d(FakerNetworkClient.TAG, msg);
        }
    }
}
