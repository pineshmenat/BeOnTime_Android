package com.its5314.project.beontime;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.its5314.project.beontime.adapter.PI_Adapter_GetShift;
import com.its5314.project.beontime.adapter.PI_POJO_GetShifts;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

public class PI_ActivityEmployeeShiftOperation extends AppCompatActivity {

    //
    // This class is for Vaishnavi or Pinesh
    //

    private List<PI_POJO_GetShifts> shifts = new ArrayList<>();
    private RecyclerView recyclerView;
    private PI_Adapter_GetShift pi_adapter_getShift;
    private Button mStartDate,mEndDate,mSubmit;
    private String startDate="1972-1-1",endDate="2019-1-1";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pi_activity_employee_shift_operation);

        mStartDate=(Button) findViewById(R.id.startDate);
        mEndDate=(Button) findViewById(R.id.endDate);
        mSubmit=(Button) findViewById(R.id.submit);


        mSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getShiftsFromDb();
            }
        });

        mStartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar calendar = Calendar.getInstance(TimeZone.getDefault());

                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(PI_ActivityEmployeeShiftOperation.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        startDate=year+"-"+(month+1)+"-"+day;
                    }
                },year,month,day);
                dialog.show();
            }
        });

        mEndDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar calendar = Calendar.getInstance(TimeZone.getDefault());

                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(PI_ActivityEmployeeShiftOperation.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        endDate=year+"-"+(month+1)+"-"+day;
                    }
                },year,month,day);
                dialog.show();
            }
        });

        // START -- Toolbar section
        Toolbar beontimeToolbar = (Toolbar) findViewById(R.id.beontimeToolbar);
        setSupportActionBar(beontimeToolbar);
        getSupportActionBar().setTitle(" BeOnTime (Employee)");
        beontimeToolbar.setLogo(R.drawable.beontime_logo_32x32);
        beontimeToolbar.setTitleTextColor(Color.WHITE);
        // END -- Toolbar section

        recyclerView = (RecyclerView) findViewById(R.id.rvShifts);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);


        getShiftsFromDb();

    }

    private void getShiftsFromDb() {

        Log.d("StartDate",startDate+","+endDate);
        PI_RequestGetShifts zf_requestGetShifts = new PI_RequestGetShifts(this, startDate, endDate, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.d("RESPONSE",response);

                try {
                    JSONObject jsonObject = new JSONObject(response);

                    if(jsonObject.getBoolean("success")){

                        JSONArray jsonArray = jsonObject.getJSONArray("shifts");

                        shifts.clear();
                        for(int i=0;i<jsonArray.length();i++){

                            JSONObject object = jsonArray.getJSONObject(i);

                            PI_POJO_GetShifts pi_pojo_getShifts = new PI_POJO_GetShifts();
                            pi_pojo_getShifts.setShiftId(object.getString("ShiftId"));
                            pi_pojo_getShifts.setCompanyName(object.getString("CompanyName"));
                            pi_pojo_getShifts.setShiftStartTime(object.getString("ShiftStartTime"));
                            pi_pojo_getShifts.setShiftEndTime(object.getString("ShiftEndTime"));

                            shifts.add(pi_pojo_getShifts);

                        }
                        pi_adapter_getShift = new PI_Adapter_GetShift(PI_ActivityEmployeeShiftOperation.this,shifts);
                        recyclerView.setAdapter(pi_adapter_getShift);


                    }
                    else{
                        Toast.makeText(PI_ActivityEmployeeShiftOperation.this,"Failed to get the shifts details",Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(zf_requestGetShifts);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.vp_menu_in_activity_employee_shift_operation, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.aboutProject: {
                Intent intent = new Intent(this, ActivityAboutProject.class);
                startActivity(intent);
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }
}
