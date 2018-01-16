package com.its5314.project.beontime;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Zhongjie FAN on 2017-12-11.
 */

public class ZF_ActivityEmployeeStartWork extends AppCompatActivity {

    String userId;
    String shiftId;
    String companyName;
    String workingPlace;
    String shiftStartTime;
    String shiftEndTime;
    String roleId;
    private SharedPreferences sharedPreferences;
    TextView tvNotYouRelogin;
    TextView tvWelcomeMsg;
    TextView tvUserId;
    TextView tvShiftDetails;
    TextView tvCompanyName;
    TextView tvShiftId;
    TextView tvWorkingPlace;
    TextView tvStartTime;
    TextView tvEndTime;
    CardView cvShift;
    Button btnStartToWork;
    Button btnSearchShift;
    double latWorkingPlace, lngWorkingPlace;
    boolean shiftSearchResult = false;
    boolean workStartStatus = false;
    public static final int reqCode = 1000;
    String fileName = "beontime_zf_status.txt";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.zf_activity_employee_start_work);

        sharedPreferences = getSharedPreferences("BE_ON_TIME",MODE_PRIVATE);
        roleId = sharedPreferences.getString("ROLE_ID","");

        // START -- Toolbar section
        Toolbar beontimeToolbar = (Toolbar) findViewById(R.id.beontimeToolbar);
        setSupportActionBar(beontimeToolbar);

        if(roleId.equalsIgnoreCase("11")){
            getSupportActionBar().setTitle(" BeOnTime (Client)");
        }
        else if(roleId.equalsIgnoreCase("12")){
            getSupportActionBar().setTitle(" BeOnTime (Employee)");
        } else {
            getSupportActionBar().setTitle(" BeOnTime (Manager)");
        }

        beontimeToolbar.setLogo(R.drawable.beontime_logo_32x32);
        beontimeToolbar.setTitleTextColor(Color.WHITE);
        // END -- Toolbar section


        tvNotYouRelogin = (TextView) findViewById(R.id.tvNotYouRelogin);
        tvWelcomeMsg = (TextView) findViewById(R.id.tvWelcomeMsg);
        tvUserId = (TextView) findViewById(R.id.tvUserId);
        btnStartToWork = (Button) findViewById(R.id.btnStartToWork);
        btnSearchShift = (Button) findViewById(R.id.btnSearchShift);
        tvShiftDetails = (TextView) findViewById(R.id.tvShiftDetails);
        cvShift = (CardView) findViewById(R.id.cvShift);
        tvCompanyName = (TextView) findViewById(R.id.tvCompanyName);
        tvShiftId = (TextView) findViewById(R.id.tvShiftId);
        tvWorkingPlace = (TextView) findViewById(R.id.tvWorkingPlace);
        tvStartTime = (TextView) findViewById(R.id.tvStartTime);
        tvEndTime = (TextView) findViewById(R.id.tvEndTime);
        cvShift.setVisibility(View.INVISIBLE);

        String firstName = getIntent().getStringExtra("firstName");
        String lastName = getIntent().getStringExtra("lastName");
        userId = getIntent().getStringExtra("userId");
        String roleId = getIntent().getStringExtra("roleId");
        String role = "";
        switch (roleId) {
            case "10": {
                role = "Manager";
                break;
            }
            case "11": {
                role = "Client";
                break;
            }
            case "12": {
                role = "Employee";
                break;
            }
        }

        String message = "Welcome, " + firstName + " " + lastName;
        tvWelcomeMsg.setText(message);
        tvUserId.setText("ID: " + userId + ", Role: " + role);

        // Load user status when program goes to this activity
        this.loadUserStatus();
//        Log.w("zf_error", "workStartStatus in onCreate(): " + workStartStatus + "");

        // If user has already started his/her job, display shift info in the activity
        if (workStartStatus) {

            btnStartToWork.setText("See Your Position");

            if (shiftSearchResult) {
                cvShift.setVisibility(View.VISIBLE);
                tvCompanyName.setText(companyName);
                tvShiftId.setText("Shift Id: " + shiftId);
                tvWorkingPlace.setText("Address: " + workingPlace);
                tvStartTime.setText("Start time: " + shiftStartTime);
                tvEndTime.setText("End time: " + shiftEndTime);
            }
        }


        // START -- listener for different widgets
        btnStartToWork.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                // Initial intent
                Intent geoLocationIntent = new Intent(ZF_ActivityEmployeeStartWork.this, ZF_ActivityEmployeeGeoLocation.class);

                if (workStartStatus) {
                    // Program goes here if "Start to work" button is clicked

                    geoLocationIntent.putExtra("latWorkingPlace", latWorkingPlace);
                    geoLocationIntent.putExtra("lngWorkingPlace", lngWorkingPlace);
                    geoLocationIntent.putExtra("shiftId", shiftId);

                    ZF_ActivityEmployeeStartWork.this.startActivityForResult(geoLocationIntent, ZF_ActivityEmployeeStartWork.reqCode);

                } else if (shiftSearchResult) {
                    // Program goes here if "Start to work" button is NOT clicked

                    geoLocationIntent.putExtra("latWorkingPlace", latWorkingPlace);
                    geoLocationIntent.putExtra("lngWorkingPlace", lngWorkingPlace);
                    geoLocationIntent.putExtra("shiftId", shiftId);

                    ZF_ActivityEmployeeStartWork.this.startActivityForResult(geoLocationIntent, ZF_ActivityEmployeeStartWork.reqCode);

                    btnStartToWork.setText("See Your Position");
                    workStartStatus = true;

                    // Use volley to use time
                    saveActualWorkingStartTimeInDB();

                } else {
                    // Program goes here if "Start to work" button is clicked, but no available shift
                    Toast.makeText(ZF_ActivityEmployeeStartWork.this, "You don't have any shift", Toast.LENGTH_SHORT).show();
                }

            }

        });

        tvNotYouRelogin.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Intent registerIntent = new Intent(ZF_ActivityEmployeeStartWork.this, ZF_ActivityLogin.class);
                ZF_ActivityEmployeeStartWork.this.startActivity(registerIntent);
            }
        });

        btnSearchShift.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String currentTime = df.format(new Date()).toString();
//                Log.w("zf_error", "currentTime: " + currentTime);

                Response.Listener<String> responseListener = new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            shiftSearchResult = jsonResponse.getBoolean("success");
//                            Log.i("zf_error", Boolean.toString(success));

                            if (shiftSearchResult) {

                                tvShiftDetails.setText("Shift Details:");

                                shiftId = jsonResponse.getString("ShiftId");
                                companyName = jsonResponse.getString("CompanyName");
                                workingPlace = jsonResponse.getString("WorkingPlace");
                                shiftStartTime = jsonResponse.getString("ShiftStartTime");
                                shiftEndTime = jsonResponse.getString("ShiftEndTime");
                                latWorkingPlace = Double.parseDouble(jsonResponse.getString("Latitude"));
                                lngWorkingPlace = Double.parseDouble(jsonResponse.getString("Longitude"));

                                cvShift.setVisibility(View.VISIBLE);
                                tvCompanyName.setText(companyName);
                                tvShiftId.setText("Shift Id: " + shiftId);
                                tvWorkingPlace.setText("Address: " + workingPlace);
                                tvStartTime.setText("Start time: " + shiftStartTime);
                                tvEndTime.setText("End time: " + shiftEndTime);

                            } else {

                                tvShiftDetails.setText("Shift Details:\n" + "You don't have any shift in next 30 minutes.");

                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                };

                ZF_RequestEmployeeSearchShift employeeSearchShiftRequestZF = new ZF_RequestEmployeeSearchShift(userId, currentTime, responseListener);
                RequestQueue queue = Volley.newRequestQueue(ZF_ActivityEmployeeStartWork.this);
                queue.add(employeeSearchShiftRequestZF);

            }
        });
        // END-- listener for different widgets
    }

    // Save start time in DB
    private void saveActualWorkingStartTimeInDB() {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String actualWorkingStartTime = df.format(new Date()).toString();

        Response.Listener<String> responseListener = new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    boolean saveActualWorkingStartTimeResult = jsonResponse.getBoolean("success");
                    Log.i("zf_error", "saveActualWorkingStartTimeResult: " + Boolean.toString(saveActualWorkingStartTimeResult));

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        };

        ZF_RequestEmployeeStartWork employeeStartWorkRequestZF = new ZF_RequestEmployeeStartWork(shiftId, actualWorkingStartTime, "SaveActualWorkingStartTime", responseListener);
        RequestQueue queue = Volley.newRequestQueue(ZF_ActivityEmployeeStartWork.this);
        queue.add(employeeStartWorkRequestZF);
    }

    // Save end time in DB
    private void saveActualWorkingEndTimeInDB() {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String actualWorkingEndTime = df.format(new Date()).toString();

        Response.Listener<String> responseListener = new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    boolean saveActualWorkingEndTimeResult = jsonResponse.getBoolean("success");
                    Log.i("zf_error", "saveActualWorkingEndTimeResult: " + Boolean.toString(saveActualWorkingEndTimeResult));

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        };

        ZF_RequestEmployeeStartWork employeeStartWorkRequestZF = new ZF_RequestEmployeeStartWork(shiftId, actualWorkingEndTime, "SaveActualWorkingEndTime", responseListener);
        RequestQueue queue = Volley.newRequestQueue(ZF_ActivityEmployeeStartWork.this);
        queue.add(employeeStartWorkRequestZF);
    }

    // Save user status in local file in phone when exit
    @Override
    protected void onStop() {

        this.saveUserStatus();
        super.onStop();
    }

    // This method is for creating overflow actions (menu in action bar)
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        // Inflate menu xml file to this activity
        menuInflater.inflate(R.menu.zf_menu_in_activity_employee_start_stop_work, menu);
        return super.onCreateOptionsMenu(menu);
    }

    // This method is for the operation of overflow actions (menu in action bar)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.employeeShiftOperation: {
                Intent intent = new Intent(this, PI_ActivityEmployeeShiftOperation.class);
                startActivity(intent);
                break;
            }
            case R.id.aboutProject: {
                Intent intent = new Intent(this, ActivityAboutProject.class);
                startActivity(intent);
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    // Below method is to process the intent send back from child intent
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == RESULT_OK) {

            switch (requestCode) {

                case ZF_ActivityEmployeeStartWork.reqCode: {

                    // When user click "Take off" button in activity ZF_ActivityEmployeeGeoLocation, save end time in DB
                    this.saveActualWorkingEndTimeInDB();

                    // In the meantime, reset some variable so that views in the activity can be cleaned.
                    btnStartToWork.setText("Start To Work");
                    workStartStatus = false;
                    shiftSearchResult = false;
                    latWorkingPlace = -1;
                    lngWorkingPlace = -1;
                    cvShift.setVisibility(View.INVISIBLE);
                    break;
                }
            }
        }
    }

    // Save user status in local file. This method is called in onStop()
    // Just in case, user turns off the application when he/she is working
    private void saveUserStatus() {

        try {
//            Log.w("zf_error", "workStartStatus in saveUserStatus(): " + workStartStatus + "");

            ArrayList<String> userStatus = new ArrayList<>();
            userStatus.add(Boolean.toString(workStartStatus));
            userStatus.add(Boolean.toString(shiftSearchResult));
            userStatus.add(companyName);
            userStatus.add(shiftId);
            userStatus.add(workingPlace);
            userStatus.add(shiftStartTime);
            userStatus.add(shiftEndTime);
            userStatus.add(Double.toString(latWorkingPlace));
            userStatus.add(Double.toString(lngWorkingPlace));

            FileOutputStream fileOutputStream = openFileOutput(fileName, MODE_PRIVATE);
//            Log.w("zf_error", getFilesDir().toString());
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
            objectOutputStream.writeObject(userStatus);

            objectOutputStream.close();
            fileOutputStream.close();

        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    // Load user status in local file. This method is called in onCreate()
    private void loadUserStatus() {

        File file = new File(getFilesDir() + "/" + fileName);
//        Log.w("zf_error", file.exists() + " fileDir: " + file.getAbsolutePath());

        if (file.exists()) {

            try {
                FileInputStream fileInputStream = openFileInput(fileName);
                ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
                ArrayList<String> userStatus = (ArrayList<String>) objectInputStream.readObject();

                boolean workStartStatus = Boolean.parseBoolean(userStatus.get(0));

//                Log.w("zf_error", "workStartStatus in loadUserStatus(): " + workStartStatus + "");

                if (workStartStatus) {

                    this.workStartStatus = Boolean.parseBoolean(userStatus.get(0));
                    shiftSearchResult = Boolean.parseBoolean(userStatus.get(1));
                    companyName = userStatus.get(2);
                    shiftId = userStatus.get(3);
                    workingPlace = userStatus.get(4);
                    shiftStartTime = userStatus.get(5);
                    shiftEndTime = userStatus.get(6);
                    latWorkingPlace = Double.parseDouble(userStatus.get(7));
                    lngWorkingPlace = Double.parseDouble(userStatus.get(8));
                }


                objectInputStream.close();
                fileInputStream.close();

            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

    }

}
