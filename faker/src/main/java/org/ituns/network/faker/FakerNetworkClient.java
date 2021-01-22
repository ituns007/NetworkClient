package org.ituns.network.faker;

import org.ituns.network.core.Callback;
import org.ituns.network.core.Client;
import org.ituns.network.core.Code;
import org.ituns.network.core.Request;
import org.ituns.network.core.Response;
import org.ituns.system.concurrent.BackTask;

public final class FakerNetworkClient extends Client {
    private final FakerAdapter mFakerAdapter;

    public FakerNetworkClient(FakerConfig config) {
        super(config.logger());
        mFakerAdapter = config.adapter();
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

        FakerAdapter fakerAdapter = mFakerAdapter;
        if(fakerAdapter == null) {
            return new Response.Builder()
                    .request(request)
                    .code(Code.FAIL_REQ)
                    .message("faker adapter is null.")
                    .build();
        }

        return fakerAdapter.readNetworkResponse(request);
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

        FakerAdapter fakerAdapter = mFakerAdapter;
        if(fakerAdapter == null) {
            callback.onResponse(new Response.Builder()
                    .request(request)
                    .code(Code.FAIL_REQ)
                    .message("faker adapter is null.")
                    .build());
            return;
        }

        BackTask.post(new RequestAsyncTask(fakerAdapter, networkRequest, callback));
    }

    @Override
    public void release() {}

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
