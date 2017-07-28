package com.munchado.orderprocess.network;

import com.android.volley.Request;
import com.android.volley.Response;
import com.munchado.orderprocess.model.reservation.ArchiveReservationResponse;
import com.munchado.orderprocess.network.request.BaseRequest;
import com.munchado.orderprocess.network.volley.GsonRequest;
import com.munchado.orderprocess.network.volley.NetworkConstants;
import com.munchado.orderprocess.utils.PrefUtil;

import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by test on 28/7/17.
 */
public class GetArchiveReservationRequest  extends BaseRequest {


    private final int page;
    public String versionCode;

    public GetArchiveReservationRequest(String str,int page) {
        this.page = page;
        versionCode = str;
    }
    public String getServiceUrl() {
        return NetworkConstants.GET_ARCHIVE_RESERVATION_URL + PrefUtil.getToken()+ "&page="+page;
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
        GsonRequest<ArchiveReservationResponse> gsonRequest = new GsonRequest<>(
                Request.Method.GET, getServiceUrl(),
                ArchiveReservationResponse.class, null, listener, errorListener, getJsonRequest());
        gsonRequest.setShouldCache(false);
        gsonRequest.setHeader(getHeaders());
        return gsonRequest;
    }
}
