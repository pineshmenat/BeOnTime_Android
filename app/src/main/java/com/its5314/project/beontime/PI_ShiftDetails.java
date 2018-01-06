package com.its5314.project.beontime;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

public class PI_ShiftDetails extends AppCompatActivity {

    private TextView mCompanyName, mWorkLocation,mJobTitle,mJobDateAndTime;
    private Button mAccept,mDecline;
    private String shiftId;
    private String roleId,companyId;
    private SharedPreferences sharedPreferences;

    private RatingBar ratingBar;
    private EditText mReviewText;

    private Button mSubmitRating;

    private float rating;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pi__shift_details);

        mCompanyName=(TextView) findViewById(R.id.shiftCompanyValue);
        mWorkLocation=(TextView) findViewById(R.id.shiftWorkLocationValue);
        mJobTitle=(TextView) findViewById(R.id.shiftJobTitleValue);
        mJobDateAndTime=(TextView) findViewById(R.id.shiftWorkDateValue);

        mAccept = (Button) findViewById(R.id.acceptBtn);
        mDecline = (Button) findViewById(R.id.declineBtn);

        ratingBar = (RatingBar) findViewById(R.id.ratingBar);
        mReviewText = (EditText) findViewById(R.id.ratingText);

        mSubmitRating = (Button) findViewById(R.id.submitRating);

        mSubmitRating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String review = mReviewText.getText().toString();

                PI_RequestGetShifts pi_requestGetShifts = new PI_RequestGetShifts(shiftId, String.valueOf(rating), review, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Log.d("RESPONSE",response);
                        try{

                            JSONObject jsonObject = new JSONObject(response);
                            boolean b = jsonObject.getBoolean("success");
                            if(b){
                                Toast.makeText(PI_ShiftDetails.this,"Review and rating are updated",Toast.LENGTH_SHORT).show();
                                finish();
                            }

                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                });
                RequestQueue queue = Volley.newRequestQueue(PI_ShiftDetails.this);
                queue.add(pi_requestGetShifts);
            }
        });

        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                rating = v;
            }
        });

        sharedPreferences = getSharedPreferences("BE_ON_TIME",MODE_PRIVATE);
        shiftId = getIntent().getStringExtra("shiftId");

        companyId = sharedPreferences.getString("COMPANY_ID","");
        roleId = sharedPreferences.getString("ROLE_ID","");

        if(roleId.equalsIgnoreCase("11")){
            getInfoFromDb(shiftId, companyId);
            mAccept.setVisibility(View.GONE);
            mDecline.setVisibility(View.GONE);
        }
        else if(roleId.equalsIgnoreCase("12")){
            getInfoFromDb(shiftId);
            ratingBar.setVisibility(View.GONE);
            mReviewText.setVisibility(View.GONE);
            mSubmitRating.setVisibility(View.GONE);

        }

    }

    private void getInfoFromDb(final String shiftId, String companyId) {


        PI_RequestGetShifts pi_requestGetShifts = new PI_RequestGetShifts(shiftId, Integer.parseInt(companyId), new Response.Listener<String>() {
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
                        mJobTitle.setText(jsonObject1.getString("empDesignationName"));
                        String startTime  = jsonObject1.getString("ShiftStartTime");//dateFormat.format(new Date(jsonObject1.getString("ShiftStartTime")));
                        String endTime = jsonObject1.getString("ShiftEndTime");
                        mJobDateAndTime.setText(startTime+" to "+endTime);
                        //String endTime  = dateFormat.format(new Date(jsonObject1.getString("ShiftEndTime")));
                        //mJobDateAndTime.append(" to "+endTime);

                        String ratingStr = jsonObject1.getString("StarRating");
                        if(ratingStr!=null){
                            rating = Float.parseFloat(ratingStr);
                            ratingBar.setRating(rating);
                        }
                        String review = jsonObject1.getString("ClientReview");
                        if(review!=null){
                            mReviewText.setText(review);
                        }

                        if (jsonObject1.getString("ShiftStatus").equalsIgnoreCase("n")) {

                            mAccept.setEnabled(true);
                            mDecline.setEnabled(true);

                            mAccept.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {

                                    PI_RequestGetShifts pi_requestGetShifts1 = new PI_RequestGetShifts(shiftId, "A", new Response.Listener<String>() {
                                        @Override
                                        public void onResponse(String response) {

                                            try{

                                                JSONObject jsonObject2 = new JSONObject(response);
                                                if(jsonObject2.getBoolean("success")){
                                                    Toast.makeText(PI_ShiftDetails.this,"Shift accepted by you",Toast.LENGTH_SHORT).show();
                                                    mAccept.setEnabled(false);
                                                    mAccept.setText("Accepted");
                                                    mDecline.setVisibility(View.GONE);
                                                }

                                            }catch (Exception e){
                                                e.printStackTrace();
                                            }
                                        }
                                    });
                                    RequestQueue requestQueue = Volley.newRequestQueue(PI_ShiftDetails.this);
                                    requestQueue.add(pi_requestGetShifts1);


                                }
                            });

                            mDecline.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    PI_RequestGetShifts pi_requestGetShifts1 = new PI_RequestGetShifts(shiftId, "R", new Response.Listener<String>() {
                                        @Override
                                        public void onResponse(String response) {

                                            try{

                                                JSONObject jsonObject2 = new JSONObject(response);
                                                if(jsonObject2.getBoolean("success")){
                                                    Toast.makeText(PI_ShiftDetails.this,"Shift rejected by you",Toast.LENGTH_SHORT).show();
                                                    mAccept.setEnabled(false);
                                                    mAccept.setText("Rejected");
                                                    mDecline.setVisibility(View.GONE);
                                                }

                                            }catch (Exception e){
                                                e.printStackTrace();
                                            }
                                        }
                                    });
                                    RequestQueue requestQueue = Volley.newRequestQueue(PI_ShiftDetails.this);
                                    requestQueue.add(pi_requestGetShifts1);
                                }
                            });
                        }
                        else if(jsonObject1.getString("ShiftStatus").equalsIgnoreCase("a")){
                            mAccept.setEnabled(false);
                            mAccept.setText("Accepted");
                            mDecline.setVisibility(View.GONE);
                        }
                        else if(jsonObject1.getString("ShiftStatus").equalsIgnoreCase("r")){
                            mAccept.setEnabled(false);
                            mAccept.setText("Rejected");
                            mDecline.setVisibility(View.GONE);
                        }
                        else if(jsonObject1.getString("ShiftStatus").equalsIgnoreCase("d")){
                            mAccept.setEnabled(false);
                            mAccept.setText("Done");
                            mDecline.setVisibility(View.GONE);
                        }


                    }
                }catch (Exception e){
                    Toast.makeText(PI_ShiftDetails.this,"Failed to parse the response",Toast.LENGTH_SHORT).show();
                }
            }
        });

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(pi_requestGetShifts);
    }

    private void getInfoFromDb(final String shiftId) {


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

                        if (jsonObject1.getString("ShiftStatus").equalsIgnoreCase("n")) {

                            mAccept.setEnabled(true);
                            mDecline.setEnabled(true);

                            mAccept.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {

                                    PI_RequestGetShifts pi_requestGetShifts1 = new PI_RequestGetShifts(shiftId, "A", new Response.Listener<String>() {
                                        @Override
                                        public void onResponse(String response) {

                                            try{

                                                JSONObject jsonObject2 = new JSONObject(response);
                                                if(jsonObject2.getBoolean("success")){
                                                    Toast.makeText(PI_ShiftDetails.this,"Shift accepted by you",Toast.LENGTH_SHORT).show();
                                                    mAccept.setEnabled(false);
                                                    mAccept.setText("Accepted");
                                                    mDecline.setVisibility(View.GONE);
                                                }

                                            }catch (Exception e){
                                                e.printStackTrace();
                                            }
                                        }
                                    });
                                    RequestQueue requestQueue = Volley.newRequestQueue(PI_ShiftDetails.this);
                                    requestQueue.add(pi_requestGetShifts1);


                                }
                            });

                            mDecline.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    PI_RequestGetShifts pi_requestGetShifts1 = new PI_RequestGetShifts(shiftId, "R", new Response.Listener<String>() {
                                        @Override
                                        public void onResponse(String response) {

                                            try{

                                                JSONObject jsonObject2 = new JSONObject(response);
                                                if(jsonObject2.getBoolean("success")){
                                                    Toast.makeText(PI_ShiftDetails.this,"Shift rejected by you",Toast.LENGTH_SHORT).show();
                                                    mAccept.setEnabled(false);
                                                    mAccept.setText("Rejected");
                                                    mDecline.setVisibility(View.GONE);
                                                }

                                            }catch (Exception e){
                                                e.printStackTrace();
                                            }
                                        }
                                    });
                                    RequestQueue requestQueue = Volley.newRequestQueue(PI_ShiftDetails.this);
                                    requestQueue.add(pi_requestGetShifts1);
                                }
                            });
                        }
                        else if(jsonObject1.getString("ShiftStatus").equalsIgnoreCase("a")){
                            mAccept.setEnabled(false);
                            mAccept.setText("Accepted");
                            mDecline.setVisibility(View.GONE);
                        }
                        else if(jsonObject1.getString("ShiftStatus").equalsIgnoreCase("r")){
                            mAccept.setEnabled(false);
                            mAccept.setText("Rejected");
                            mDecline.setVisibility(View.GONE);
                        }
                        else if(jsonObject1.getString("ShiftStatus").equalsIgnoreCase("d")){
                            mAccept.setEnabled(false);
                            mAccept.setText("Done");
                            mDecline.setVisibility(View.GONE);
                        }


                    }
                }catch (Exception e){
                    Toast.makeText(PI_ShiftDetails.this,"Failed to parse the response",Toast.LENGTH_SHORT).show();
                }
            }
        });

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(pi_requestGetShifts);
    }
}
