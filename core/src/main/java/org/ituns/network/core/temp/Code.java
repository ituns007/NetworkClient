package org.ituns.network.core.temp;

public class Code {
    public static final int FAIL_REQ = -3;
    public static final int FAIL_RESP = -2;
    public static final int FAIL_PARSE = -1;

    public static boolean isSuccessful(int code) {
        return 200 <= code && code <= 299;
    }
}
