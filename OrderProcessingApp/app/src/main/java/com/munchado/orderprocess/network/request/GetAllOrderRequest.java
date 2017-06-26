package com.munchado.orderprocess.network.request;

import com.android.volley.Request;
import com.android.volley.Response;
import com.munchado.orderprocess.model.archiveorder.AllOrderResponse;
import com.munchado.orderprocess.network.volley.GsonRequest;
import com.munchado.orderprocess.network.volley.NetworkConstants;
import com.munchado.orderprocess.utils.PrefUtil;

import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by munchado on 24/6/17.
 */

public class GetAllOrderRequest extends BaseRequest {

    public String versionCode;
    String from_date, to_date;

    public GetAllOrderRequest(String str, String fdate, String tdate) {
        versionCode = str;
        from_date = fdate;
        to_date = tdate;
    }

    public String getServiceUrl() {

        return NetworkConstants.GET_ORDER_LIST_DATE_URL + PrefUtil.getToken() + "&type=all&current_version=" + versionCode + "&fromDate=" + from_date + "&toDate=" + to_date;
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
        GsonRequest<AllOrderResponse> gsonRequest = new GsonRequest<>(
                Request.Method.GET, getServiceUrl(),
                AllOrderResponse.class, null, listener, errorListener, getJsonRequest());
        gsonRequest.setShouldCache(false);
        gsonRequest.setHeader(getHeaders());
        return gsonRequest;
    }
}
