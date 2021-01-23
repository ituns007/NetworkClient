package org.ituns.network.okhttp.stable;

import org.ituns.network.core.Request;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import okhttp3.Headers;
import okhttp3.RequestBody;

public class OkHttpRequest {

    public static okhttp3.Request create(Request request) {
        if(request == null) {
            return null;
        }

        try {
            String method = request.method().name();
            RequestBody requestBody = new OkHttpBuffer(request.body()).okhttpBody();
            return new okhttp3.Request.Builder()
                    .url(request.url())
                    .headers(Headers.of(buildHeaderArray(request)))
                    .method(method, requestBody)
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static String[] buildHeaderArray(Request request) {
        if(request == null) {
            return new String[]{};
        }

        ArrayList<String> list = new ArrayList<>();
        Map<String, List<String>> headers = request.headers();
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
