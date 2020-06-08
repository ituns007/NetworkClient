package org.ituns.network.core;

import org.ituns.toolset.loger.LogerClient;
import org.ituns.toolset.loger.Priority;

public abstract class NetworkClient {
    private static final String TAG = "NetworkClient";

    private final LogerClient mLogerClient;

    public NetworkClient() {
        mLogerClient = new LogerClient(TAG);
        mLogerClient.setDebug(false);
        mLogerClient.setPriority(Priority.VERBOSE);
    }

    public final void setDebug(boolean isDebug) {
        mLogerClient.setDebug(isDebug);
    }

    public final NetworkResponse requestSync(NetworkRequest request) {
        NetworkUtils.printNetworkRequest(mLogerClient, request);
        NetworkResponse response = onRequestSync(request);
        NetworkUtils.printNetworkResponse(mLogerClient, response);
        return response;
    }

    protected abstract NetworkResponse onRequestSync(NetworkRequest request);

    public final void requestAsync(NetworkRequest request, NetworkCallback callback) {
        NetworkUtils.printNetworkRequest(mLogerClient, request);
        onRequestAsync(request, new NetworkCallbackImpl(mLogerClient, callback));
    }

    protected abstract void onRequestAsync(NetworkRequest request, NetworkCallback callback);

    public final void release() {
        mLogerClient.release();
        onRelease();
    }

    protected abstract void onRelease();

    private static class NetworkCallbackImpl implements NetworkCallback {
        private LogerClient mLogerClient;
        private NetworkCallback mNetworkCallback;

        public NetworkCallbackImpl(LogerClient logerClient, NetworkCallback networkCallback) {
            this.mLogerClient = logerClient;
            this.mNetworkCallback = networkCallback;
        }

        @Override
        public void onSuccess(NetworkResponse response) {
            NetworkUtils.printNetworkResponse(mLogerClient, response);
            NetworkCallback networkCallback = mNetworkCallback;
            if(networkCallback != null) {
                networkCallback.onSuccess(response);
            }
            releaseCallback();
        }

        @Override
        public void onError(NetworkResponse response) {
            NetworkUtils.printNetworkResponse(mLogerClient, response);
            NetworkCallback networkCallback = mNetworkCallback;
            if(networkCallback != null) {
                networkCallback.onError(response);
            }
            releaseCallback();
        }

        private void releaseCallback() {
            mNetworkCallback = null;
            mLogerClient = null;
        }
    }
}
