package com.munchado.orderprocess.network.volley;

import com.android.volley.NetworkError;

public interface RequestCallback<T> {
    public void error(NetworkError networkError);

    public void success(Object obj);
}
