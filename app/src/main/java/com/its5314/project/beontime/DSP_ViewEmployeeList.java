package com.its5314.project.beontime;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class DSP_ViewEmployeeList extends AppCompatActivity {

    private String companyId;
    private Intent intent;

    RecyclerView rv;
   // RecyclerView.Adapter adapter;
    EmployeeEngine employeeEngine;
    List<Employee> emp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dsp_activity_view_employee_list);

        // START -- Toolbar section
        Toolbar beontimeToolbar = (Toolbar) findViewById(R.id.beontimeToolbar);
        setSupportActionBar(beontimeToolbar);
        getSupportActionBar().setTitle(" BeOnTime (Manager)");
        beontimeToolbar.setLogo(R.drawable.beontime_logo_32x32);
        beontimeToolbar.setTitleTextColor(Color.WHITE);
        //END -- Toolbar section

        companyId = getIntent().getStringExtra("companyId");
        rv = (RecyclerView) findViewById(R.id.rv);
        emp = new ArrayList<>();
        rv.setLayoutManager(new LinearLayoutManager(this));
        populateEmployeeList();
        /*for(int i = 0 ; i < 10 ; i++){
            emp.add(new Employee("id " + i,"name " + i,"email " +i,"address "+ i));
        }*/

/*        employeeEngine = new EmployeeEngine(emp);
        rv = (RecyclerView) findViewById(R.id.rv);

        rv.setLayoutManager(new LinearLayoutManager(this));
        adapter = new EmployeeAdapter(employeeEngine);
        rv.setAdapter(adapter);*/
    }



    private void populateEmployeeList() {
        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try{
                    JSONObject jsonObject = new JSONObject(response);
                    if(jsonObject.getBoolean("success")){
                        //ArrayList<Employee> emp = new ArrayList<>();
                        JSONArray jsonArray = jsonObject.getJSONArray("employees");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject json = jsonArray.getJSONObject(i);
                            Log.i("dsp",json.getString("employeeId")+"");
                            emp.add(new Employee(json.getString("employeeId")+"",
                                                 json.getString("firstName")+" "+json.getString("lastName"),
                                                 json.getString("email")+"",
                                                 json.getString("address")+", "+
                                                         json.getString("city")+", "+
                                                         json.getString("province")+" - "+
                                                         json.getString("postalCode")+", "));
                        }
                        employeeEngine = new EmployeeEngine(emp);
                        /*rv = (RecyclerView) findViewById(R.id.rv);
                        rv.setLayoutManager(new LinearLayoutManager(DSP_ViewEmployeeList.this));*/
                        RecyclerView.Adapter adapter = new EmployeeAdapter(employeeEngine);
                        rv.setAdapter(adapter);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        DSP_RequestEmployeeList dsp_requestEmployeeList = new DSP_RequestEmployeeList(companyId,responseListener);
        RequestQueue requestQueue = Volley.newRequestQueue(DSP_ViewEmployeeList.this);
        requestQueue.add(dsp_requestEmployeeList);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        // Inflate menu xml file to this activity
        menuInflater.inflate(R.menu.dsp_menu_in_activity_employee_position, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.viewProfile:
                intent = new Intent(DSP_ViewEmployeeList.this, DSP_ViewManagerProfile.class);
                intent.putExtra("companyId", companyId);
                DSP_ViewEmployeeList.this.startActivity(intent);
                break;
            case R.id.LogOut:
                intent = new Intent(DSP_ViewEmployeeList.this, ZF_ActivityLogin.class);
                DSP_ViewEmployeeList.this.startActivity(intent);
                break;
            case R.id.aboutProject:
                intent = new Intent(this, ActivityAboutProject.class);
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
