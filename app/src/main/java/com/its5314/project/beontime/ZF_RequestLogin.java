package com.its5314.project.beontime;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Zhongjie FAN on 2017-12-20.
 */

public class ZF_RequestLogin extends StringRequest {

   // public static final String HTTP_REQUEST_URL = "http://192.168.126.140/samba/all/php_course/test/mobile_login/zf_android_request.php";
    public static final String HTTP_REQUEST_URL = "http://beontime.byethost16.com/android_project/zf_android_request.php";

    // NOTES:
    // Below cookie string is only effective for 1 day
    // To get new cookie code, please visit http://beontime.byethost16.com/android_project/getcookie.php from web browser
   public static final String BYETHOST_COOKIE_FOR_SECURITY = "0f908067562d857a4bd94b6ee1997ffe"; // For Emulator
//    public static final String BYETHOST_COOKIE_FOR_SECURITY = "24a0eaef28ccd808e4353cabdb6215fe"; // For Samsung Galaxy

    private Map<String, String> params;

    public ZF_RequestLogin(String username, String password, Response.Listener<String> listener) {
        super(Request.Method.POST, ZF_RequestLogin.HTTP_REQUEST_URL, listener, null);

        params = new HashMap<>();
        params.put("operation", "LoginAuthentication");
        params.put("username", username);
        params.put("password", password);
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
        // emulator
//        headers.put("Cookie", "__test=a4dfa9fda7b6709e61a1933cf8092ba8; expires=Thu, 31-Dec-37 23:55:55 GMT; path=/");
        // samsung galaxy
        headers.put("Cookie", "__test=" + ZF_RequestLogin.BYETHOST_COOKIE_FOR_SECURITY + "; expires=Thu, 31-Dec-37 23:55:55 GMT; path=/");


        return headers;
    }
}
