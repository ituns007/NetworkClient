package org.ituns.network.core;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public abstract class Client {
    private final Logcat mLogcat;

    public Client(Logger logger) {
        mLogcat = new Logcat(logger);
    }

    public final Response requestSync(Request request) {
        mLogcat.printRequest(request);
        Response response = onRequestSync(request);
        mLogcat.printResponse(response);
        return response;
    }

    protected abstract Response onRequestSync(Request request);

    public final void requestAsync(Request request, Callback callback) {
        mLogcat.printRequest(request);
        onRequestAsync(request, new CallbackProxy(mLogcat, callback));
    }

    protected abstract void onRequestAsync(Request request, Callback callback);

    public abstract void release();

    private static class CallbackProxy implements Callback {
        private Logcat mLogcat;
        private Callback mCallback;

        public CallbackProxy(Logcat logcat, Callback callback) {
            mLogcat = logcat;
            mCallback = callback;
        }

        @Override
        public void onResponse(Response response) {
            mLogcat.printResponse(response);
            Callback callback = mCallback;
            if(callback != null) {
                callback.onResponse(response);
                mCallback = null;
            }
        }
    }

    private static class Logcat {
        private Logger logger;

        public Logcat(Logger logger) {
            this.logger = logger;
        }

        public void printRequest(Request request) {
            if(request == null) {
                printLog("Request:Error=>request is null.");
                return;
            }

            printLog("Request:URL =>" + request.url());
            printLog("Request:METHOD =>" + request.method().name());

            Map<String, List<String>> headers = request.headers();
            for(String key : headers.keySet()) {
                List<String> value = headers.get(key);
                printLog("Request:HEADER=>" + key + ":" + buildHeaderText(value));
            }

            Request.Body body = request.body();
            if(body != null) {
                printLog("Request:BODY=>" + parseRequestBody(body));
            }
        }

        public void printResponse(Response response) {
            if(response == null) {
                printLog("Response:Error=>response is null.");
                return;
            }

            Request request = response.request();
            if(request == null) {
                printLog("Response:Error=>request is null.");
                return;
            }

            printLog("Response:URL=>" + request.url());
            printLog("Response:CODE=>" + response.code());
            printLog("Response:MSG=>" + response.message());

            Map<String, List<String>> headers = response.headers();
            for(String key : headers.keySet()) {
                List<String> value = headers.get(key);
                printLog("Response:HEADER=>" + key + ":" + buildHeaderText(value));
            }

            Response.Body body = response.body();
            if(body != null) {
                printLog("Response:BODY=>" + parseResponseBody(body));
            }
        }

        private String buildHeaderText(List<String> headers) {
            StringBuilder builder = new StringBuilder();
            for(String header : headers) {
                if(builder.length() > 0) {
                    builder.append(",");
                }
                builder.append(header);
            }
            return builder.toString();
        }

        private String parseRequestBody(Request.Body body) {
            if(body == null) {
                return "";
            }

            try {
                Buffer buffer = new Buffer();
                body.writeTo(buffer);
                return buffer.readUtf8();
            } catch (IOException e) {
                printException(e);
                return "";
            }
        }

        private String parseResponseBody(Response.Body body) {
            if(body == null) {
                return "";
            }

            try {
                return body.string();
            } catch (IOException e) {
                printException(e);
                return "";
            }
        }

        private void printLog(String msg) {
            if(logger != null) {
                logger.log(msg);
            }
        }

        private void printException(Throwable t) {
            if(logger != null) {
                logger.log(t);
            }
        }
    }
}
