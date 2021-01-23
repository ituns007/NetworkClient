package org.ituns.network.faker;

import android.content.Context;
import android.text.TextUtils;

import org.ituns.network.core.Code;
import org.ituns.network.core.MediaType;
import org.ituns.network.core.Request;
import org.ituns.network.core.Response;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public final class FakerAdapter {
    private FakerUrl mFakerUrl;
    private final WeakReference<Context> mContext;

    private FakerAdapter(Builder builder) {
        this.mFakerUrl = builder.fakerUrl;
        this.mContext = new WeakReference<>(builder.context);
    }

    Response readNetworkResponse(Request request) {
        if(request == null) {
            return new Response.Builder()
                    .request(request)
                    .code(Code.FAIL_REQ)
                    .message("request is null.")
                    .build();
        }

        Context context = mContext.get();
        if(context == null) {
            return new Response.Builder()
                    .request(request)
                    .code(Code.FAIL_REQ)
                    .message("context is null.")
                    .build();
        }

        FakerUrl fakerUrl = mFakerUrl;
        if(fakerUrl == null) {
            return new Response.Builder()
                    .request(request)
                    .code(Code.FAIL_REQ)
                    .message("faker url is null.")
                    .build();
        }

        String resource = fakerUrl.proceed(request.url());
        if(TextUtils.isEmpty(resource)) {
            return new Response.Builder()
                    .request(request)
                    .code(Code.FAIL_REQ)
                    .message("resource is empty.")
                    .build();
        }

        try {
            String resourceData = readAssetsResouce(context, resource);

            JSONObject jsonData = new JSONObject(resourceData);

            return parseResponseFromJson(request, jsonData);
        } catch (Exception e) {
            return new Response.Builder()
                    .request(request)
                    .code(Code.FAIL_RESP)
                    .message("exception:" + e.getMessage())
                    .build();
        }
    }

    private Response parseResponseFromJson(Request request, JSONObject json) {
        if(json == null) {
            return new Response.Builder()
                    .request(request)
                    .code(Code.FAIL_RESP)
                    .message("json data is null.")
                    .build();
        }

        Response.Builder builder = new Response.Builder();
        builder.request(request);
        builder.code(json.optInt("code", Code.FAIL_RESP));
        builder.message(json.optString("message", "Non Message!"));
        builder.headers(parseHeaderFromJson(json.optJSONObject("header")));
        builder.body(Response.Body.create(MediaType.parse("application/json"),
                json.optString("body", "")));
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

    void release() {
        mContext.clear();
        FakerUrl fakerUrl = mFakerUrl;
        if(fakerUrl != null) {
            fakerUrl.release();
            mFakerUrl = null;
        }
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
