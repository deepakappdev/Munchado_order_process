package com.munchado.orderprocess.network.request;

import com.android.volley.Request;
import com.android.volley.Response;
import com.munchado.orderprocess.model.orderprocess.OrderProcessResponse;
import com.munchado.orderprocess.network.volley.GsonRequest;
import com.munchado.orderprocess.network.volley.NetworkConstants;
import com.munchado.orderprocess.utils.PrefUtil;
import com.munchado.orderprocess.utils.StringUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by user on 6/23/2015.
 */
public class OrderProcessRequest extends BaseRequest{

    private final String orderId;
    private final String status;
    private final String reason;
    private final String delivery_time;

    public OrderProcessRequest(String orderId, String status, String reason, String deliverytime) {
        this.orderId = orderId;
        this.status = status;
        this.reason = reason;
        this.delivery_time = deliverytime;
    }

    public String getServiceUrl() {
        return NetworkConstants.GET_ORDER_DETAIL_URL + orderId + "?mob=true&token="+ PrefUtil.getToken();
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
        try {
            object.put("status", status);
            object.put("reason", reason);
            if(!StringUtils.isNullOrEmpty(delivery_time))
                object.put("delivery_date", delivery_time);
        } catch (JSONException e) {}
        return object;
    }

    public GsonRequest createServerRequest(Response.ErrorListener errorListener, Response.Listener listener) {
        GsonRequest<OrderProcessResponse> gsonRequest = new GsonRequest<>(
                Request.Method.PUT, getServiceUrl(),
                OrderProcessResponse.class, null, listener, errorListener, getJsonRequest());
        gsonRequest.setShouldCache(false);
        gsonRequest.setHeader(getHeaders());
        return gsonRequest;
    }
}
