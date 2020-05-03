package org.ituns.network.core;

public interface NetworkCallback {

    void onSuccess(NetworkResponse response);

    void onError(NetworkResponse response);
}
