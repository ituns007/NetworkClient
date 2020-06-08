package org.ituns.network.faker;

import org.ituns.network.core.NetworkCallback;
import org.ituns.network.core.NetworkClient;
import org.ituns.network.core.NetworkCode;
import org.ituns.network.core.NetworkRequest;
import org.ituns.network.core.NetworkResponse;
import org.ituns.system.concurrent.BackTask;

public final class FakerNetworkClient extends NetworkClient {
    private final FakerAdapter mFakerAdapter;

    public FakerNetworkClient(FakerAdapter fakeAdapter) {
        mFakerAdapter = fakeAdapter;
    }

    @Override
    protected NetworkResponse onRequestSync(NetworkRequest networkRequest) {
        if(networkRequest == null) {
            return new NetworkResponse.Builder(networkRequest)
                    .code(NetworkCode.ERROR_REQUEST)
                    .message("network request is null.")
                    .build();
        }

        FakerAdapter fakerAdapter = mFakerAdapter;
        if(fakerAdapter == null) {
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
            callback.onError(new NetworkResponse.Builder(networkRequest)
                    .code(NetworkCode.ERROR_REQUEST)
                    .message("network request is null.")
                    .build());
            return;
        }

        FakerAdapter fakerAdapter = mFakerAdapter;
        if(fakerAdapter == null) {
            callback.onError(new NetworkResponse.Builder(networkRequest)
                    .code(NetworkCode.ERROR_REQUEST)
                    .message("faker adapter is null.")
                    .build());
            return;
        }

        BackTask.post(new RequestAsyncTask(fakerAdapter, networkRequest, callback));
    }

    @Override
    protected void onRelease() {}

    private static class RequestAsyncTask implements Runnable {
        private FakerAdapter mFakerAdapter;
        private NetworkRequest mNetworkRequest;
        private NetworkCallback mNetworkCallback;

        public RequestAsyncTask(FakerAdapter fakerAdapter, NetworkRequest networkRequest, NetworkCallback networkCallback) {
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
            mNetworkCallback = null;
            mNetworkRequest = null;
            mFakerAdapter = null;
        }
    }
}
