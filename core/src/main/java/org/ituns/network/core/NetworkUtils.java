package org.ituns.network.core;

import android.util.Log;

import java.util.List;
import java.util.Map;

class NetworkUtils {
    private static String TAG = "NetworkClient";

    public static void printNetworkRequest(NetworkRequest networkRequest, boolean debugMode) {
        if(!debugMode || networkRequest == null) {
            return;
        }

        logcat("Request URL=>" + networkRequest.url());
        logcat("Request METHOD=>" + networkRequest.method().text());

        Map<String, List<String>> headers = networkRequest.headers();
        for(String key : headers.keySet()) {
            List<String> value = headers.get(key);
            logcat("Request HEADER=>" + key + ":" + buildHeaderText(value));
        }

        String body = parseRequestBody(networkRequest);
        if(body != null) {
            logcat("Request BODY=>" + body);
        }
    }

    public static void printNetworkResponse(NetworkResponse networkResponse, boolean debugMode) {
        if(!debugMode || networkResponse == null) {
            return;
        }

        NetworkRequest networkRequest = networkResponse.request();
        if(networkRequest == null) {
            return;
        }

        logcat("Response URL=>" + networkRequest.url());
        logcat("Response CODE=>" + networkResponse.code());
        logcat("Response MSG=>" + networkResponse.message());

        Map<String, List<String>> headers = networkResponse.headers();
        for(String key : headers.keySet()) {
            List<String> value = headers.get(key);
            logcat("Response HEADER=>" + key + ":" + buildHeaderText(value));
        }

        logcat("Response BODY=>" + parseResponseBody(networkResponse));
    }

    private static void logcat(String msg) {
        Log.d(TAG, msg);
    }

    private static String buildHeaderText(List<String> headers) {
        StringBuilder builder = new StringBuilder();
        for(String header : headers) {
            if(builder.length() > 0) {
                builder.append(",");
            }
            builder.append(header);
        }
        return builder.toString();
    }

    private static String parseRequestBody(NetworkRequest networkRequest) {
        if(networkRequest == null) {
            return null;
        }

        byte[] body = networkRequest.body();
        NetworkMethod method = networkRequest.method();
        if(method == NetworkMethod.GET || method == NetworkMethod.HEAD) {
            if(body == null) {
                return null;
            }
        } else {
            if(body == null) {
                return "";
            }
        }
        return new String(body);
    }

    private static String parseResponseBody(NetworkResponse networkResponse) {
        if(networkResponse == null) {
            return "";
        }

        byte[] body = networkResponse.body();
        if(body == null) {
            return "";
        }

        return new String(body);
    }
}
