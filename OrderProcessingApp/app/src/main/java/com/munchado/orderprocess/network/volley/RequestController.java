package com.munchado.orderprocess.network.volley;

import android.content.Context;

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
import com.munchado.orderprocess.network.request.GetFlickerImageRequest;

import java.util.concurrent.TimeUnit;

public class RequestController {
    public static final String TAG = "RequestController";
    private static final int DEFAULT_MAX_RETRIES = 2;
    private static RetryPolicy rp = new DefaultRetryPolicy((int) TimeUnit.SECONDS.toMillis(20), DEFAULT_MAX_RETRIES,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);


    public static RetryPolicy getRetryPolicy() {
        return rp;
    }

    private static Response.ErrorListener getErrorListener(final RequestCallback<?> callback) {
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                if (callback != null)
                    callback.error(new NetworkError(volleyError));

            }
        };
    }

    private static Response.Listener getListener(final RequestCallback<?> callback) {
        return new Response.Listener() {
            @Override
            public void onResponse(Object response) {
                if (response == null)
                    callback.error(new NetworkError(new Throwable("Something went wrong")));
                else
                    callback.success(response);
            }
        };
    }

//    public static void getFlickerImage(Context context, RequestCallback callBack) {
//        GetFlickerImageRequest categoryRequest = new GetFlickerImageRequest();
//        GsonRequest request = categoryRequest.createServerRequest(getErrorListener(callBack), getListener(callBack));
//        Cache cache = new DiskBasedCache(context.getCacheDir(), 1024 * 1024 / 8);
//        Network network = new BasicNetwork(new HurlStack());
//        RequestQueue mRequestQueue = new RequestQueue(cache, network);
//        mRequestQueue.start();
//        mRequestQueue.add(request);
//    }
}
