package com.its5314.project.beontime;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class DSP_ViewManagerProfile extends AppCompatActivity {
    private TextView tvCompanyId;
    private TextView tvCompanyName;
    private TextView tvCompanyEmail;
    private TextView tvCompanyURL;
    private TextView tvCompanyAddress;
    private String companyId;
    private TextView websiteTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dsp_activity_view_manager_profile);

        // START -- Toolbar section
        Toolbar beontimeToolbar = (Toolbar) findViewById(R.id.beontimeToolbar);
        setSupportActionBar(beontimeToolbar);
        getSupportActionBar().setTitle(" BeOnTime (Manager)");
        beontimeToolbar.setLogo(R.drawable.beontime_logo_32x32);
        beontimeToolbar.setTitleTextColor(Color.WHITE);
        //END -- Toolbar section

        tvCompanyId = findViewById(R.id.tvCompanyId);
        tvCompanyName = findViewById(R.id.tvCompanyName);
        tvCompanyEmail = findViewById(R.id.tvCompanyEmail);
        tvCompanyURL = findViewById(R.id.tvCompanyURL);
        tvCompanyAddress = findViewById(R.id.tvCompanyAddress);
        companyId = getIntent().getStringExtra("companyId");
        websiteTV = findViewById(R.id.websiteTV);
        websiteTV.setMovementMethod(LinkMovementMethod.getInstance());
        websiteTV.setText(Html.fromHtml("<a href='http://beontime.byethost16.com'>http://beontime.byethost16.com</a>"));
        populateProfileInfo();
    }

    private void populateProfileInfo() {
        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getBoolean("success")) {
                        JSONObject json = jsonObject.getJSONArray("profile").getJSONObject(0);
                        tvCompanyId.setText("ID : " + json.getString("CompanyId"));
                        tvCompanyName.setText("Name : " + json.getString("CompanyName"));
                        tvCompanyEmail.setText("Email : " + json.getString("CompanyEmail"));
                        tvCompanyURL.setText("URL : " + json.getString("CompanyURL"));
                        tvCompanyAddress.setText("Address : " + json.getString("CompanyStreetNumber") + ", "
                                + json.getString("CompanyStreetName") + ", " +
                                json.getString("CompanyCity") + ", "+json.getString("CompanyState")
                                +" - "+ json.getString("CompanyPostal") + " "+
                                json.getString("CompanyCountry"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        DSP_RequestManagerProfile dsp_requestManagerProfile = new DSP_RequestManagerProfile(companyId, responseListener);
        RequestQueue requestQueue = Volley.newRequestQueue(DSP_ViewManagerProfile.this);
        requestQueue.add(dsp_requestManagerProfile);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        // Inflate menu xml file to this activity
        menuInflater.inflate(R.menu.dsp_menu_in_activity_employee_position, menu);
        return super.onCreateOptionsMenu(menu);
    }

}
