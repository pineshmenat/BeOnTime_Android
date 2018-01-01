package com.its5314.project.beontime;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

public class PI_ShiftDetails extends AppCompatActivity {

    private TextView mCompanyName, mWorkLocation,mJobTitle,mJobDateAndTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pi__shift_details);

        mCompanyName=(TextView) findViewById(R.id.shiftCompanyValue);
        mWorkLocation=(TextView) findViewById(R.id.shiftWorkLocationValue);
        mJobTitle=(TextView) findViewById(R.id.shiftJobTitleValue);
        mJobDateAndTime=(TextView) findViewById(R.id.shiftWorkDateValue);

        String shiftId = getIntent().getStringExtra("shiftId");
        getInfoFromDb(shiftId);
    }

    private void getInfoFromDb(String shiftId) {

        PI_RequestGetShifts pi_requestGetShifts = new PI_RequestGetShifts(shiftId, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("RESPONSE",response);

                try {

                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                    JSONObject jsonObject = new JSONObject(response);
                    if(jsonObject.getBoolean("success")){

                        JSONArray jsonArray = jsonObject.getJSONArray("shifts");
                        JSONObject jsonObject1 = jsonArray.getJSONObject(0);

                        mCompanyName.setText(jsonObject1.getString("CompanyName"));
                        mWorkLocation.setText(jsonObject1.getString("Address")+", "+jsonObject1.getString("City")+", "+jsonObject1.getString("PostalCode"));
                        mJobTitle.setText("-");
                        String startTime  = jsonObject1.getString("ShiftStartTime");//dateFormat.format(new Date(jsonObject1.getString("ShiftStartTime")));
                        String endTime = jsonObject1.getString("ShiftEndTime");
                        mJobDateAndTime.setText(startTime+" to "+endTime);
                        //String endTime  = dateFormat.format(new Date(jsonObject1.getString("ShiftEndTime")));
                        //mJobDateAndTime.append(" to "+endTime);

                    }
                }catch (Exception e){

                }
            }
        });

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(pi_requestGetShifts);
    }
}
