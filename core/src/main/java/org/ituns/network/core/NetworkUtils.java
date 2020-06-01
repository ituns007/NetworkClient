package org.ituns.network.core;

import org.ituns.toolset.loger.LogerClient;

import java.util.List;
import java.util.Map;

class NetworkUtils {

    public static void printNetworkRequest(LogerClient loger, NetworkRequest networkRequest) {
        if(networkRequest == null) {
            loger.d("Request Error => network request is null.");
            return;
        }

        loger.d("Request URL =>" + networkRequest.url());
        loger.d("Request METHOD =>" + networkRequest.method().text());

        Map<String, List<String>> headers = networkRequest.headers();
        for(String key : headers.keySet()) {
            List<String> value = headers.get(key);
            loger.d("Request HEADER =>" + key + ":" + buildHeaderText(value));
        }

        String body = parseRequestBody(networkRequest);
        if(body != null) {
            loger.d("Request BODY =>" + body);
        }
    }

    public static void printNetworkResponse(LogerClient loger, NetworkResponse networkResponse) {
        if(networkResponse == null) {
            loger.d("Response Error => network response is null.");
            return;
        }

        NetworkRequest networkRequest = networkResponse.request();
        if(networkRequest == null) {
            loger.d("Response Error => network request is null.");
            return;
        }

        loger.d("Response URL =>" + networkRequest.url());
        loger.d("Response CODE =>" + networkResponse.code());
        loger.d("Response MSG =>" + networkResponse.message());

        Map<String, List<String>> headers = networkResponse.headers();
        for(String key : headers.keySet()) {
            List<String> value = headers.get(key);
            loger.d("Response HEADER =>" + key + ":" + buildHeaderText(value));
        }

        if(networkResponse.body() != null) {
            loger.d("Response BODY =>" + parseResponseBody(networkResponse));
        }
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
