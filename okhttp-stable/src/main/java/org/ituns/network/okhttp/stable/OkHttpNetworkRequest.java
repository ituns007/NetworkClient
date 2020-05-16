package org.ituns.network.okhttp.stable;

import org.ituns.network.core.NetworkMethod;
import org.ituns.network.core.NetworkRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;

class OkHttpNetworkRequest {
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    private NetworkRequest mNetworkRequest;

    private OkHttpNetworkRequest(NetworkRequest networkRequest) {
        mNetworkRequest = networkRequest;
    }

    public static OkHttpNetworkRequest create(NetworkRequest networkRequest) {
        return new OkHttpNetworkRequest(networkRequest);
    }

    public Request okhttp() {
        NetworkRequest networkRequest = mNetworkRequest;
        if(networkRequest == null) {
            return null;
        }

        try {
            Request.Builder builder = new Request.Builder();

            //config url
            builder.url(networkRequest.url());

            //config headers
            builder.headers(Headers.of(parseHeadersArray(networkRequest)));

            //config method and data
            NetworkMethod networkMethod = networkRequest.method();
            if(networkMethod == null) {
                builder.get();
            } else if(networkMethod == NetworkMethod.GET) {
                builder.get();
            } else if(networkMethod == NetworkMethod.POST) {
                builder.post(RequestBody.create(networkRequest.body(), JSON));
            } else if(networkMethod == NetworkMethod.HEAD) {
                builder.head();
            } else if(networkMethod == NetworkMethod.DELETE) {
                builder.delete(RequestBody.create(networkRequest.body(), JSON));
            } else if(networkMethod == NetworkMethod.PATCH) {
                builder.patch(RequestBody.create(networkRequest.body(), JSON));
            } else if(networkMethod == NetworkMethod.PUT) {
                builder.put(RequestBody.create(networkRequest.body(), JSON));
            }
            return builder.build();
        } catch (Exception e) {
            return null;
        }
    }

    private String[] parseHeadersArray(NetworkRequest networkRequest) {
        if(networkRequest == null) {
            return new String[]{};
        }

        ArrayList<String> list = new ArrayList<>();
        Map<String, List<String>> headers = networkRequest.headers();
        for(String key : headers.keySet()) {
            List<String> values = headers.get(key);
            if(values == null) {
                continue;
            }

            for(String value : values) {
                list.add(key);
                list.add(value);
            }
        }

        return list.toArray(new String[list.size()]);
    }
}
