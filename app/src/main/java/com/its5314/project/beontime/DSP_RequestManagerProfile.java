package com.its5314.project.beontime;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by imsil on 4/1/18.
 */

public class DSP_RequestManagerProfile extends StringRequest {
    private final HashMap<String, String> params;

    public DSP_RequestManagerProfile(String companyId,Response.Listener<String> listener) {
        super(Method.POST, ZF_RequestLogin.HTTP_REQUEST_URL, listener, null);
        params = new HashMap<>();
        params.put("operation", "getCompanyProfile");
        params.put("companyId", companyId);

    }

    @Override
    public Map<String, String> getParams() {
        return params;
    }

    public Map<String, String> getHeaders() throws AuthFailureError {
        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("User-Agent", "Mozilla/5.0");
        headers.put("Accept-Language", "en-US,en;q=0.5");
        // Below cookie is because of the security mechanism in Byethost website
        headers.put("Cookie", "__test=" + ZF_RequestLogin.BYETHOST_COOKIE_FOR_SECURITY + "; expires=Thu, 31-Dec-37 23:55:55 GMT; path=/");

        return headers;
    }
}
