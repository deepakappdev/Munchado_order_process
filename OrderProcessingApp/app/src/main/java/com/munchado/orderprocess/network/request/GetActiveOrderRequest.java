package com.munchado.orderprocess.network.request;

import com.android.volley.Request;
import com.android.volley.Response;
import com.munchado.orderprocess.model.archiveorder.ActiveOrderResponse;
import com.munchado.orderprocess.network.volley.GsonRequest;
import com.munchado.orderprocess.network.volley.NetworkConstants;
import com.munchado.orderprocess.utils.LogUtils;
import com.munchado.orderprocess.utils.PrefUtil;

import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by user on 6/23/2015.
 */
public class GetActiveOrderRequest extends BaseRequest{

    public String getServiceUrl() {
        LogUtils.d("=== url : "+NetworkConstants.GET_ORDER_URL + "&token="+ PrefUtil.getToken()+"&type=live");
        return NetworkConstants.GET_ORDER_URL + "&token="+ PrefUtil.getToken()+"&type=live";
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
        GsonRequest<ActiveOrderResponse> itemListRequest = new GsonRequest<>(
                Request.Method.GET, getServiceUrl(),
                ActiveOrderResponse.class, null, listener, errorListener, getJsonRequest());
        itemListRequest.setShouldCache(false);
        itemListRequest.setHeader(getHeaders());
        return itemListRequest;
    }
}
