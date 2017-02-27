package com.munchado.orderprocess.network.request;

import com.android.volley.Request;
import com.android.volley.Response;
import com.munchado.orderprocess.model.orderdetail.OrderDetailResponse;
import com.munchado.orderprocess.network.volley.GsonRequest;
import com.munchado.orderprocess.network.volley.NetworkConstants;
import com.munchado.orderprocess.utils.LogUtils;
import com.munchado.orderprocess.utils.PrefUtil;

import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by user on 6/23/2015.
 */
public class GetOrderDetailRequest extends BaseRequest {

    private final String orderId;

    public GetOrderDetailRequest(String orderId) {
        this.orderId = orderId;
    }

    public String getServiceUrl() {
        return NetworkConstants.GET_ORDER_DETAIL_URL + orderId + "?mob=true&token=" + PrefUtil.getToken();
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
        LogUtils.d("=== url : ", getServiceUrl());
        GsonRequest<OrderDetailResponse> gsonRequest = new GsonRequest<>(
                Request.Method.GET, getServiceUrl(),
                OrderDetailResponse.class, null, listener, errorListener, getJsonRequest());
        gsonRequest.setShouldCache(false);
        gsonRequest.setHeader(getHeaders());
        return gsonRequest;
    }
}
