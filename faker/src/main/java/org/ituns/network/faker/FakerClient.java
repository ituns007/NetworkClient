package org.ituns.network.faker;

import android.os.Handler;
import android.os.HandlerThread;

import org.ituns.network.core.Callback;
import org.ituns.network.core.Client;
import org.ituns.network.core.Code;
import org.ituns.network.core.Request;
import org.ituns.network.core.Response;

public final class FakerClient extends Client {
    private Handler mHandler;
    private FakerAdapter mAdapter;

    public FakerClient(FakerConfig config) {
        super(config);
        mHandler = initHandler();
        mAdapter = config.adapter();
    }

    private Handler initHandler() {
        HandlerThread thread = new HandlerThread("FakerClient");
        thread.start();
        return new Handler(thread.getLooper());
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

        FakerAdapter adapter = mAdapter;
        if(adapter == null) {
            return new Response.Builder()
                    .request(request)
                    .code(Code.FAIL_REQ)
                    .message("faker adapter is null.")
                    .build();
        }

        return adapter.readNetworkResponse(request);
    }

    @Override
    protected void onRequestAsync(Request request, Callback callback) {
        if(request == null) {
            callback.onResponse(new Response.Builder()
                    .request(request)
                    .code(Code.FAIL_REQ)
                    .message("request is null.")
                    .build());
            return;
        }

        FakerAdapter adapter = mAdapter;
        if(adapter == null) {
            callback.onResponse(new Response.Builder()
                    .request(request)
                    .code(Code.FAIL_REQ)
                    .message("faker adapter is null.")
                    .build());
            return;
        }

        Handler handler = mHandler;
        if(handler == null) {
            callback.onResponse(new Response.Builder()
                    .request(request)
                    .code(Code.FAIL_REQ)
                    .message("handler is null.")
                    .build());
            return;
        }

        handler.post(new RequestAsyncTask(adapter, request, callback));
    }

    @Override
    public void release() {
        FakerAdapter adapter = mAdapter;
        if(adapter != null) {
            adapter.release();
            mAdapter = null;
        }
        Handler handler = mHandler;
        if(handler != null) {
            handler.getLooper().quit();
            mHandler = null;
        }
    }

    private static class RequestAsyncTask implements Runnable {
        private FakerAdapter mAdapter;
        private Request mRequest;
        private Callback mCallback;

        public RequestAsyncTask(FakerAdapter adapter, Request request, Callback callback) {
            mAdapter = adapter;
            mRequest = request;
            mCallback = callback;
        }

        @Override
        public void run() {
            Request request = mRequest;
            Callback callback = mCallback;
            if(callback == null) {
                releaseAsyncTask();
                return;
            }

            FakerAdapter adapter = mAdapter;
            if(adapter == null) {
                callback.onResponse(new Response.Builder()
                        .request(request)
                        .code(Code.FAIL_REQ)
                        .message("faker adapter is null.")
                        .build());
                releaseAsyncTask();
                return;
            }

            Response response = adapter.readNetworkResponse(request);
            callback.onResponse(response);
            releaseAsyncTask();
        }

        private void releaseAsyncTask() {
            mCallback = null;
            mRequest = null;
            mAdapter = null;
        }
    }
}
