package org.ituns.network.okhttp.compat;

import org.ituns.network.core.NetworkCode;
import org.ituns.network.core.NetworkRequest;
import org.ituns.network.core.NetworkResponse;

import java.io.IOException;

import okhttp3.Response;
import okhttp3.ResponseBody;

class OkHttpNetworkResponse {
    private NetworkRequest mNetworkRequest;

    private OkHttpNetworkResponse(NetworkRequest networkRequest) {
        mNetworkRequest = networkRequest;
    }

    public static OkHttpNetworkResponse create(NetworkRequest networkRequest) {
        return new OkHttpNetworkResponse(networkRequest);
    }

    public NetworkResponse okhttp(Response response) throws IOException {
        NetworkRequest networkRequest = mNetworkRequest;
        if(response == null) {
            return new NetworkResponse.Builder(networkRequest)
                    .code(NetworkCode.ERROR_RESPONSE)
                    .message("okhttp response is null.")
                    .build();
        }

        NetworkResponse.Builder builder =  new NetworkResponse.Builder(networkRequest);
        builder.code(response.code());
        builder.message(response.message());
        builder.headers(response.headers().toMultimap());
        ResponseBody responseBody = response.body();
        if(responseBody != null) {
            builder.body(responseBody.bytes());
        }
        return builder.build();
    }
}
