package com.munchado.orderprocess.network.request;

import com.android.volley.Request;
import com.android.volley.Response;
import com.munchado.orderprocess.model.BaseResponse;
import com.munchado.orderprocess.network.volley.GsonRequest;
import com.munchado.orderprocess.network.volley.NetworkConstants;
import com.munchado.orderprocess.utils.PrefUtil;

import org.json.JSONObject;

import java.util.HashMap;

import static android.R.attr.id;

/**
 * Created by test on 24/7/17.
 */

public class GetReservationDetailRequest extends BaseRequest {

    String resrvationid;
    public GetReservationDetailRequest(String id){
    resrvationid = id;
    }
    public String getServiceUrl() {

        return NetworkConstants.GET_RESERVATION_DETAIL_URL +id+"?mob=true&token="+ PrefUtil.getToken();
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
        GsonRequest<BaseResponse> gsonRequest = new GsonRequest<>(
                Request.Method.GET, getServiceUrl(),
                BaseResponse.class, null, listener, errorListener, getJsonRequest());
        gsonRequest.setShouldCache(false);
        gsonRequest.setHeader(getHeaders());
        return gsonRequest;
    }
}
