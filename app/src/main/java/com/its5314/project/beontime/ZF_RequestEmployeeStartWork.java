package com.its5314.project.beontime;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Zhongjie FAN on 2017-12-23.
 */

public class ZF_RequestEmployeeStartWork extends StringRequest {

    private Map<String, String> params;

    public ZF_RequestEmployeeStartWork(String shiftId, String time, String operationType, Response.Listener<String> listener) {
        super(Request.Method.POST, ZF_RequestLogin.HTTP_REQUEST_URL, listener, null);

        // Save parameters in a map
        params = new HashMap<>();

        if(operationType.equalsIgnoreCase("SaveActualWorkingStartTime")) {

            params.put("operation", "SaveActualWorkingStartTime");

        } else if(operationType.equalsIgnoreCase("SaveActualWorkingEndTime")) {

            params.put("operation", "SaveActualWorkingEndTime");
        }

        params.put("shiftId", shiftId);
        params.put("time", time);
    }

    @Override
    public Map<String, String> getParams() {
        return params;
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {

        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("User-Agent", "Mozilla/5.0");
        headers.put("Accept-Language", "en-US,en;q=0.5");
        // Below cookie is because of the security mechanism in Byethost website
        headers.put("Cookie", "__test=" + ZF_RequestLogin.BYETHOST_COOKIE_FOR_SECURITY + "; expires=Thu, 31-Dec-37 23:55:55 GMT; path=/");

        return headers;
    }
}
