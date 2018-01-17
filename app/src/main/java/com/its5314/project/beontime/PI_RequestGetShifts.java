package com.its5314.project.beontime;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Zhongjie FAN on 2017-12-20.
 */

public class PI_RequestGetShifts extends StringRequest {

    //public static final String HTTP_REQUEST_URL = "http://192.168.126.140/samba/all/php_course/test/mobile_login/zf_android_request.php";
    public static final String HTTP_REQUEST_URL = "http://beontime.byethost16.com/android_project/zf_android_request.php";

    // NOTES:
    // Below cookie string is only effective for 1 day
    // To get new cookie code, please visit http://beontime.byethost16.com/android_project/getcookie.php from web browser
    public static final String BYETHOST_COOKIE_FOR_SECURITY = "3cd8d1e6ca7e6c6f76ad8f003f13c604"; // For Emulator
    //public static final String BYETHOST_COOKIE_FOR_SECURITY = "2136669357b0b82d9a3f516cb2b130d9"; // For Samsung Galaxy

    private Map<String, String> params;

    private SharedPreferences sharedPreferences;

    /*public PI_RequestGetShifts(Context context, Response.Listener<String> listener) {
        super(Method.POST, PI_RequestGetShifts.HTTP_REQUEST_URL, listener, null);


        sharedPreferences = context.getSharedPreferences("BE_ON_TIME",Context.MODE_PRIVATE);

        params = new HashMap<>();
        params.put("operation", "getShifts");
        params.put("userId", sharedPreferences.getString("USER_ID",""));
        //params.put("password", password);
    }*/

    public PI_RequestGetShifts(Context context, String startTime, String endTime, String roleId, Response.Listener<String> listener) {
        super(Method.POST, PI_RequestGetShifts.HTTP_REQUEST_URL, listener, null);


        sharedPreferences = context.getSharedPreferences("BE_ON_TIME",Context.MODE_PRIVATE);

        if(roleId.equalsIgnoreCase("12")) {
            params = new HashMap<>();
            params.put("operation", "filterShifts");
            params.put("userId", sharedPreferences.getString("USER_ID", ""));
            params.put("startDate", startTime);
            params.put("endDate", endTime);
        }
        else {
            params = new HashMap<>();
            params.put("operation", "getClientShifts");
            params.put("companyId", sharedPreferences.getString("COMPANY_ID", ""));
            params.put("startDate", startTime);
            params.put("endDate", endTime);
        }
    }

    public PI_RequestGetShifts(String shiftID, Response.Listener<String> listener) {
        super(Method.POST, PI_RequestGetShifts.HTTP_REQUEST_URL, listener, null);

        params = new HashMap<>();
        params.put("operation", "getShiftDetails");
        params.put("shiftId", shiftID);

    }

    public PI_RequestGetShifts(String shiftID, int companyId, Response.Listener<String> listener) {
        super(Method.POST, PI_RequestGetShifts.HTTP_REQUEST_URL, listener, null);

        Log.d("RESPONSE",shiftID+" , "+companyId);
        params = new HashMap<>();
        params.put("operation", "getClientShiftDetails");
        params.put("shiftId", shiftID);
        params.put("companyId", String.valueOf(companyId));
    }

    public PI_RequestGetShifts(String shiftID, String status, Response.Listener<String> listener) {
        super(Method.POST, PI_RequestGetShifts.HTTP_REQUEST_URL, listener, null);

        params = new HashMap<>();
        params.put("operation", "changeStatus");
        params.put("shiftId", shiftID);
        params.put("status", status);

    }

    public PI_RequestGetShifts(String shiftID, String rating,String review, Response.Listener<String> listener) {
        super(Method.POST, PI_RequestGetShifts.HTTP_REQUEST_URL, listener, null);

        params = new HashMap<>();
        params.put("operation", "updateRatingAndReview");
        params.put("shiftId", shiftID);
        params.put("StarRating", rating);
        params.put("ClientReview", review);

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
        headers.put("Cookie", "__test=" + PI_RequestGetShifts.BYETHOST_COOKIE_FOR_SECURITY + "; expires=Thu, 31-Dec-37 23:55:55 GMT; path=/");


        return headers;
    }
}
