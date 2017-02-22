package com.munchado.orderprocess.network.volley;

import com.android.volley.NetworkError;

public interface RequestCallback {
    void error(NetworkError networkError);

    void success(Object obj);
}
