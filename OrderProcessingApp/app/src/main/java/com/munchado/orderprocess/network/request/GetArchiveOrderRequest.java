package com.munchado.orderprocess.network.request;

import com.android.volley.Request;
import com.android.volley.Response;
import com.munchado.orderprocess.network.volley.GsonRequest;

import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by user on 6/23/2015.
 */
public class GetArchiveOrderRequest {

    public String getServiceUrl() {
        return "https://api.flickr.com/services/feeds/photos_public.gne?format=json&nojsoncallback=?";
    }

    public HashMap<String, String> getParameters() {
        HashMap parameter = new HashMap();
        return parameter;
    }

    public HashMap<String, String> getHeaders() {
        HashMap<String, String> header = new HashMap<String, String>();
        return header;
    }

    public JSONObject getJsonRequest() {
        JSONObject object = new JSONObject();
        return object;
    }

    public GsonRequest createServerRequest(Response.ErrorListener errorListener, Response.Listener listener) {
        GsonRequest<Object> itemListRequest = new GsonRequest<>(
                Request.Method.GET, getServiceUrl(),
                Object.class, null, listener, errorListener, getJsonRequest());
        itemListRequest.setShouldCache(false);
        itemListRequest.setHeader(getHeaders());
        return itemListRequest;
    }
}
