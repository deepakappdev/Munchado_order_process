package com.munchado.orderprocess.network.request;

import com.android.volley.Request;
import com.android.volley.Response;
import com.munchado.orderprocess.model.dinein.DineinConfirmResponse;
import com.munchado.orderprocess.network.volley.GsonRequest;
import com.munchado.orderprocess.network.volley.NetworkConstants;
import com.munchado.orderprocess.utils.PrefUtil;
import com.munchado.orderprocess.utils.StringUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by munchado on 29/4/17.
 */
public class UpdateDineRequest extends BaseRequest {

    String reservationId;
    String user_id;
    String status;
    String offer;
    String restaurant_instruction;
    String hold_time;

    public UpdateDineRequest(String user_id, String reservationId, String status, String offer, String restaurant_instruction, String hold_time) {
        this.reservationId = reservationId;
        this.user_id = user_id;
        this.status = status;
        this.offer = offer;
        this.restaurant_instruction = restaurant_instruction;
        this.hold_time = hold_time;
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

//       user_id=2779&status=3&offer=Hispanic Heritage $38 Special for One&restaurant_instruction=Hey, we are testing update of hold a table&hold_time=20
        try {
            object.put("user_id", user_id);
            object.put("status", status);
            if (!StringUtils.isNullOrEmpty(restaurant_instruction))
                object.put("restaurant_instruction", restaurant_instruction);
            if (!StringUtils.isNullOrEmpty(offer))
                object.put("offer", offer);
            if (!StringUtils.isNullOrEmpty(hold_time))
                object.put("hold_time", hold_time);
            else
                object.put("hold_time", "00");
        } catch (JSONException e) {
        }
        return object;
    }

    public GsonRequest createServerRequest(Response.ErrorListener errorListener, Response.Listener listener) {
        GsonRequest<DineinConfirmResponse> gsonRequest = new GsonRequest<>(
                Request.Method.PUT, getServiceUrl(),
                DineinConfirmResponse.class, null, listener, errorListener, getJsonRequest());
        gsonRequest.setShouldCache(false);
        gsonRequest.setHeader(getHeaders());
        return gsonRequest;
    }
}

