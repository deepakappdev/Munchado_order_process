package com.munchado.orderprocess.network.request;

import com.android.volley.Request;
import com.android.volley.Response;
import com.munchado.orderprocess.model.update.UpgradeData;
import com.munchado.orderprocess.network.volley.GsonRequest;
import com.munchado.orderprocess.network.volley.NetworkConstants;
import com.munchado.orderprocess.utils.PrefUtil;

import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by munchado on 21/3/17.
 */
public class UpdateAppRequest  extends BaseRequest{

    private final String version;

    public UpdateAppRequest(String version){
        this.version=version;
    }

    public String getServiceUrl() {
        return NetworkConstants.GET_UPDATE_APP_URL + "&token="+ PrefUtil.getToken()+"&current_version="+version;
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
        GsonRequest<UpgradeData> gsonRequest = new GsonRequest<>(
                Request.Method.GET, getServiceUrl(),
                UpgradeData.class, null, listener, errorListener, getJsonRequest());
        gsonRequest.setShouldCache(false);
        gsonRequest.setHeader(getHeaders());
        return gsonRequest;
    }
}
