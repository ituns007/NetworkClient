package org.ituns.network.core;

public abstract class NetworkClient {
    protected boolean isDebugMode;

    public NetworkClient() {
        isDebugMode = false;
    }

    public NetworkClient(boolean isDebugMode) {
        this.isDebugMode = isDebugMode;
    }

    public final NetworkResponse requestSync(NetworkRequest request) {
        NetworkUtils.printNetworkRequest(request, isDebugMode);
        NetworkResponse response = onRequestSync(request);
        NetworkUtils.printNetworkResponse(response, isDebugMode);
        return response;
    }

    protected abstract NetworkResponse onRequestSync(NetworkRequest request);

    public final void requestAsync(NetworkRequest request, NetworkCallback callback) {
        NetworkUtils.printNetworkRequest(request, isDebugMode);
        onRequestAsync(request, new NetworkCallbackImpl(callback, isDebugMode));
    }

    protected abstract void onRequestAsync(NetworkRequest request, NetworkCallback callback);

    private static class NetworkCallbackImpl implements NetworkCallback {
        private boolean isDebugMode;
        private NetworkCallback mNetworkCallback;

        public NetworkCallbackImpl(NetworkCallback networkCallback, boolean isDebugMode) {
            this.isDebugMode = isDebugMode;
            this.mNetworkCallback = networkCallback;
        }

        @Override
        public void onSuccess(NetworkResponse response) {
            NetworkUtils.printNetworkResponse(response, isDebugMode);
            NetworkCallback networkCallback = mNetworkCallback;
            if(networkCallback != null) {
                networkCallback.onSuccess(response);
            }
        }

        @Override
        public void onError(NetworkResponse response) {
            NetworkUtils.printNetworkResponse(response, isDebugMode);
            NetworkCallback networkCallback = mNetworkCallback;
            if(networkCallback != null) {
                networkCallback.onError(response);
            }
        }
    }
}
