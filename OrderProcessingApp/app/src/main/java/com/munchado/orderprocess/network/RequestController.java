package com.munchado.orderprocess.network;

import com.android.volley.Cache;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Network;
import com.android.volley.NetworkError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.munchado.orderprocess.MyApplication;
import com.munchado.orderprocess.model.BaseResponse;
import com.munchado.orderprocess.model.token.TokenResponse;
import com.munchado.orderprocess.network.request.BaseRequest;
import com.munchado.orderprocess.network.request.GetActiveOrderRequest;
import com.munchado.orderprocess.network.request.GetArchiveOrderRequest;
import com.munchado.orderprocess.network.request.LoginRequest;
import com.munchado.orderprocess.network.request.NewTokenRequest;
import com.munchado.orderprocess.network.volley.GsonRequest;
import com.munchado.orderprocess.network.volley.RequestCallback;
import com.munchado.orderprocess.utils.PrefUtil;

import java.util.concurrent.TimeUnit;

public class RequestController {
    public static final String TAG = "RequestController";
    private static final int DEFAULT_MAX_RETRIES = 2;
    private static RetryPolicy rp = new DefaultRetryPolicy((int) TimeUnit.SECONDS.toMillis(60), DEFAULT_MAX_RETRIES,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
    private static RequestQueue mRequestQueue;


    public static RetryPolicy getRetryPolicy() {
        return rp;
    }

    private static Response.ErrorListener getErrorListener(final RequestCallback callback) {
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                if (callback != null)
                    callback.error(new NetworkError(volleyError));

            }
        };
    }

    private static Response.Listener getListener(final RequestCallback callback, final BaseRequest request) {
        return new Response.Listener() {
            @Override
            public void onResponse(Object response) {
                if (response == null)
                    callback.error(new NetworkError(new Throwable("Something went wrong")));
                else if (response instanceof BaseResponse) {
                    if (((BaseResponse) response).is_token_valid) {
                        callback.success(response);
                    } else {
                        RequestController.getNewAccessToken(callback, request);
                    }
                }

            }
        };
    }

    public static void getNewAccessToken(final RequestCallback lastCallback, final BaseRequest request) {
        NewTokenRequest newTokenRequest = new NewTokenRequest();
        GsonRequest authRequest = newTokenRequest.createServerRequest(getErrorListener(lastCallback),
                new Response.Listener() {
                    @Override
                    public void onResponse(Object response) {
                        if (response instanceof TokenResponse) {
                            TokenResponse authResponse = (TokenResponse) response;
                            if (authResponse.is_token_valid) {
                                PrefUtil.putToken(authResponse.data.token);
                                getmRequestQueue().add(request.createServerRequest(getErrorListener(lastCallback), getListener(lastCallback, request)));
                            }
                        }
                    }
                });
        getmRequestQueue().add(authRequest);
    }

    public static synchronized RequestQueue getmRequestQueue() {
        if (mRequestQueue != null) return mRequestQueue;
        Cache cache = new DiskBasedCache(MyApplication.mContext.getCacheDir(), 1024 * 1024); // 1/8MB cap
        //System.setProperty("http.proxyHost", "192.168.2.5");//for charles
        //System.setProperty("http.proxyPort", "8888");
        Network network = new BasicNetwork(new HurlStack());
        mRequestQueue = new RequestQueue(cache, network);
        mRequestQueue.start();
        return mRequestQueue;
    }

    public static void createNewAccessToken(RequestCallback callBack) {
        NewTokenRequest request = new NewTokenRequest();
        GsonRequest gsonRequest = request.createServerRequest(getErrorListener(callBack), getListener(callBack, request));
        getmRequestQueue().add(gsonRequest);
    }
    public static void login(String username, String password, RequestCallback callBack) {
        LoginRequest request = new LoginRequest(username, password);
        GsonRequest gsonRequest = request.createServerRequest(getErrorListener(callBack), getListener(callBack, request));
        getmRequestQueue().add(gsonRequest);
    }
    public static void logout(String username, String password, RequestCallback callBack) {
        LoginRequest request = new LoginRequest(username, password);
        GsonRequest gsonRequest = request.createServerRequest(getErrorListener(callBack), getListener(callBack, request));
        getmRequestQueue().add(gsonRequest);
    }
    public static void getArchiveOrder(RequestCallback callBack) {
        GetArchiveOrderRequest request = new GetArchiveOrderRequest();
        GsonRequest gsonRequest = request.createServerRequest(getErrorListener(callBack), getListener(callBack, request));
        getmRequestQueue().add(gsonRequest);
    }

    public static void getActiveOrder(RequestCallback callBack) {
        GetActiveOrderRequest request = new GetActiveOrderRequest();
        GsonRequest gsonRequest = request.createServerRequest(getErrorListener(callBack), getListener(callBack, request));
        getmRequestQueue().add(gsonRequest);
    }


}
