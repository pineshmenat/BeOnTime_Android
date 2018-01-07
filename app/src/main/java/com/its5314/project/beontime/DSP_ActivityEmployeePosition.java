package com.its5314.project.beontime;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
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
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class DSP_ActivityEmployeePosition extends AppCompatActivity implements
        OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener {
    private static final int MY_LOCATION_PERMISSION_REQUEST_CODE = 1;
    private static final int LOCATION_LAYER_PERMISSION_REQUEST_CODE = 2;
    GoogleMap gMap;
    private String companyId;
    private TextView welcomeTV;
    private TextView companyIdTV;
    Intent intent = null;
    Button btnEmpList;
    final ArrayList<Coordinates> coordinates = new ArrayList<>();
    private boolean mLocationPermissionDenied = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dsp_activity_employee_position);

        // START -- Toolbar section
        Toolbar beontimeToolbar = (Toolbar) findViewById(R.id.beontimeToolbar);
        setSupportActionBar(beontimeToolbar);
        getSupportActionBar().setTitle(" BeOnTime (Manager)");
        beontimeToolbar.setLogo(R.drawable.beontime_logo_32x32);
        beontimeToolbar.setTitleTextColor(Color.WHITE);
        //END -- Toolbar section

        welcomeTV = findViewById(R.id.welcomeTV);
        companyIdTV = findViewById(R.id.companyIdTV);
        btnEmpList = findViewById(R.id.btnEmpList);
        String firstName = getIntent().getStringExtra("firstName");
        String lastName = getIntent().getStringExtra("lastName");
        String userId = getIntent().getStringExtra("userId");
        String roleId = getIntent().getStringExtra("roleId");
        companyId = getIntent().getStringExtra("companyId");
        initMap();
        getLatLng();
        btnEmpList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(DSP_ActivityEmployeePosition.this,DSP_ViewEmployeeList.class);
                intent.putExtra("companyId",companyId);
                DSP_ActivityEmployeePosition.this.startActivity(intent);
            }
        });
    }

    private void getLatLng() {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String currentTime = df.format(new Date()).toString();
        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    companyIdTV.setText(companyId);
                    JSONObject jsonObject = new JSONObject(response);
                    double lat = 0, lng = 0;
                    if (jsonObject.getBoolean("success")) {
                        JSONArray jsonArray = jsonObject.getJSONArray("positions");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject json = jsonArray.getJSONObject(i);
                            Coordinates coordinate = new Coordinates(
                                    Double.parseDouble(json.getString("CurrentLat")),
                                    Double.parseDouble(json.getString("CurrentLong")));
                            if (i == 0) {
                                welcomeTV.setText("Welcome! " + json.get("CompanyName"));
                            }
                            coordinates.add(coordinate);
                            Log.i("dsp", coordinates.get(i).getLatitude() + " " +
                                    coordinates.get(i).getLongitude());
                            gMap.addMarker(new MarkerOptions()
                                    .position(new LatLng(coordinates.get(i).getLatitude(),
                                            coordinates.get(i).getLongitude()))
                                    .title(json.getString("FirstName") + " " + json.getString("LastName"))
                                    .snippet(json.getString("EmployeeId")));
                            lat += coordinates.get(i).getLatitude();
                            lng += coordinates.get(i).getLongitude();
                        }
                        lat /= coordinates.size();
                        lng /= coordinates.size();
                        gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lng), 10));
                    } else {
                        Toast.makeText(DSP_ActivityEmployeePosition.this, "No employees currently working", Toast.LENGTH_LONG).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        DSP_RequestEmployeePosition dsp_requestEmployeePosition = new DSP_RequestEmployeePosition(companyId, currentTime, responseListener);
        RequestQueue requestQueue = Volley.newRequestQueue(DSP_ActivityEmployeePosition.this);
        requestQueue.add(dsp_requestEmployeePosition);
    }

    private void initMap() {
        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.mapManager);
        mapFragment.getMapAsync(this);
    }


    //This method is for creating overflow actions (menu in action bar)
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        // Inflate menu xml file to this activity
        menuInflater.inflate(R.menu.dsp_menu_in_activity_employee_position, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.i("dsp", "on map ready called");
        gMap = googleMap;
        gMap.getUiSettings().setMyLocationButtonEnabled(true);
        gMap.setMyLocationEnabled(true);
        gMap.getUiSettings().setZoomControlsEnabled(true);
        gMap.getUiSettings().setZoomGesturesEnabled(true);
    }

    /**
     * Requests the fine location permission. If a rationale with an additional explanation should
     * be shown to the user, displays a dialog that triggers the request.
     */

    public void requestLocationPermission(int requestCode) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.ACCESS_FINE_LOCATION)) {
            // Display a dialog with rationale.
            PermissionUtils.RationaleDialog
                    .newInstance(requestCode, false).show(
                    getSupportFragmentManager(), "dialog");
        } else {
            // Location permission has not been granted yet, request it.
            PermissionUtils.requestPermission(this, requestCode,
                    Manifest.permission.ACCESS_FINE_LOCATION, false);
        }
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == MY_LOCATION_PERMISSION_REQUEST_CODE) {
            // Enable the My Location button if the permission has been granted.
            if (PermissionUtils.isPermissionGranted(permissions, grantResults,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                gMap.getUiSettings().setMyLocationButtonEnabled(true);

            } else {
                mLocationPermissionDenied = true;
            }
        } else if (requestCode == LOCATION_LAYER_PERMISSION_REQUEST_CODE) {
            // Enable the My Location layer if the permission has been granted.
            if (PermissionUtils.isPermissionGranted(permissions, grantResults,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                gMap.setMyLocationEnabled(true);
            } else {
                mLocationPermissionDenied = true;
            }
        }
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        Toast.makeText(this, "Info window clicked",
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.viewProfile:
                intent = new Intent(DSP_ActivityEmployeePosition.this, DSP_ViewManagerProfile.class);
                intent.putExtra("companyId",companyId);
                DSP_ActivityEmployeePosition.this.startActivity(intent);
                break;
            case R.id.LogOut:
                intent = new Intent(DSP_ActivityEmployeePosition.this, ZF_ActivityLogin.class);
                DSP_ActivityEmployeePosition.this.startActivity(intent);
                break;
            case R.id.aboutProject:
                intent = new Intent(this, ActivityAboutProject.class);
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
