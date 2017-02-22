package com.munchado.orderprocess.network.volley;

import com.android.volley.NetworkError;

public interface RequestCallback<T> {
    void error(NetworkError networkError);

    void success(Object obj);
}
