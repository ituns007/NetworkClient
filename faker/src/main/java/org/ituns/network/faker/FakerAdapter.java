package org.ituns.network.faker;

import android.content.Context;
import android.text.TextUtils;

import org.ituns.network.core.NetworkCode;
import org.ituns.network.core.NetworkRequest;
import org.ituns.network.core.NetworkResponse;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class FakerAdapter {
    private Context mContext;
    private FakerUrl mFakerUrl;

    private FakerAdapter(Builder builder) {
        this.mContext = builder.context;
        this.mFakerUrl = builder.fakerUrl;
    }

    public NetworkResponse readNetworkResponse(NetworkRequest networkRequest) {
        if(networkRequest == null) {
            return new NetworkResponse.Builder(networkRequest)
                    .code(NetworkCode.ERROR_REQUEST)
                    .message("network request is null.")
                    .build();
        }

        Context context = mContext;
        if(context == null) {
            return new NetworkResponse.Builder(networkRequest)
                    .code(NetworkCode.ERROR_RESPONSE)
                    .message("context is null.")
                    .build();
        }

        FakerUrl fakerUrl = mFakerUrl;
        if(fakerUrl == null) {
            return new NetworkResponse.Builder(networkRequest)
                    .code(NetworkCode.ERROR_RESPONSE)
                    .message("faker url is null.")
                    .build();
        }

        String resource = fakerUrl.proceed(networkRequest.url());
        if(TextUtils.isEmpty(resource)) {
            return new NetworkResponse.Builder(networkRequest)
                    .code(NetworkCode.ERROR_RESPONSE)
                    .message("response resource is empty.")
                    .build();
        }

        try {
            String resourceData = readAssetsResouce(context, resource);

            JSONObject jsonData = new JSONObject(resourceData);

            return parseResponseFromJson(networkRequest, jsonData);
        } catch (Exception e) {
            return new NetworkResponse.Builder(networkRequest)
                    .code(NetworkCode.ERROR_RESPONSE)
                    .message("exception:" + e.getMessage())
                    .build();
        }
    }

    private NetworkResponse parseResponseFromJson(NetworkRequest networkRequest, JSONObject json) {
        if(json == null) {
            return new NetworkResponse.Builder(networkRequest)
                    .code(NetworkCode.ERROR_RESPONSE)
                    .message("json data is null.")
                    .build();
        }

        NetworkResponse.Builder builder = new NetworkResponse.Builder(networkRequest);
        builder.code(json.optInt("code", NetworkCode.ERROR_RESPONSE));
        builder.message(json.optString("message", "Non Message!"));
        builder.headers(parseHeaderFromJson(json.optJSONObject("header")));
        builder.body(json.optString("body", "").getBytes());
        return builder.build();
    }

    private Map<String, List<String>> parseHeaderFromJson(JSONObject json) {
        Map<String, List<String>> header = new HashMap<>();
        if(json == null) {
            return header;
        }

        Iterator<String> iterator = json.keys();
        while (iterator.hasNext()) {
            String key = iterator.next();
            JSONArray array = json.optJSONArray(key);
            if(array == null) {
                continue;
            }

            for(int i = 0; i < array.length(); i++) {
                List<String> list = header.get(key);
                if(list == null) {
                    list = new ArrayList<>();
                    header.put(key, list);
                }
                list.add(array.optString(i));
            }
        }

        return header;
    }

    private String readAssetsResouce(Context context, String resource) throws Exception {
        int len = 0;
        byte[] buffer = new byte[2048];
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        InputStream is = context.getAssets().open(resource);
        while ((len = is.read(buffer)) != -1) {
            baos.write(buffer, 0, len);
            baos.flush();
        }
        byte[] bytes = baos.toByteArray();
        baos.close();
        is.close();
        return new String(bytes, "UTF-8");
    }

    public static class Builder {
        private Context context;
        private FakerUrl fakerUrl;

        public Builder(Context context) {
            this.context = context;
            fakerUrl = new FakerUrl();
        }

        public Builder append(String url, String resource) {
            fakerUrl.append(url, resource);
            return this;
        }

        public FakerAdapter build() {
            return new FakerAdapter(this);
        }
    }
}
