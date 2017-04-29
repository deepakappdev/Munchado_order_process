package com.munchado.orderprocess.network.request;

import com.android.volley.Request;
import com.android.volley.Response;
import com.munchado.orderprocess.model.dinein.DineinDetailResponse;
import com.munchado.orderprocess.network.volley.GsonRequest;
import com.munchado.orderprocess.network.volley.NetworkConstants;
import com.munchado.orderprocess.utils.PrefUtil;

import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by munchado on 29/4/17.
 */
public class GetDineinDetailRequest extends BaseRequest {

    String reservationId;

    public GetDineinDetailRequest(String reservationId) {
        this.reservationId = reservationId;
    }

    public String getServiceUrl() {

        String url = String.format(NetworkConstants.GET_DINEIN_DETAIL_URL, reservationId);
        return url + PrefUtil.getToken();
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
        GsonRequest<DineinDetailResponse> gsonRequest = new GsonRequest<>(
                Request.Method.GET, getServiceUrl(),
                DineinDetailResponse.class, null, listener, errorListener, getJsonRequest());
        gsonRequest.setShouldCache(false);
        gsonRequest.setHeader(getHeaders());
        return gsonRequest;
    }
}
