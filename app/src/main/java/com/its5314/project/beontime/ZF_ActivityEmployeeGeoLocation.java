package com.its5314.project.beontime;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.*;
import android.text.style.*;
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
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
import org.json.JSONObject;

public class ZF_ActivityEmployeeGeoLocation extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {

    TextView tvGeoInfo;
    TextView tvPositionInfoLable;
    Button btnTakeOff;
    GoogleMap mGoogleMap;
    GoogleApiClient mGoogleApiClient;
    LocationRequest mLocationRequest;
    Marker workingPlaceMarker;
    Circle workingMovementRange;
    double latWorkingPlace, lngWorkingPlace;
    double latUserPosition, lngUserPosition;
    String shiftId;

    final float zoom = 16;      // Google map zoom
    final int gpsUpdateInterval = 2500;  // unit is millisecond
    final int radiusOfMovement = 100;   // unit is meter

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.zf_activity_employee_geo_location);

        // START -- Toolbar section
        Toolbar beontimeToolbar = (Toolbar) findViewById(R.id.beontimeToolbar);
        setSupportActionBar(beontimeToolbar);
        getSupportActionBar().setTitle(" BeOnTime (Employee)");
        beontimeToolbar.setLogo(R.drawable.beontime_logo_32x32);
        beontimeToolbar.setTitleTextColor(Color.WHITE);
        // END -- Toolbar section

        btnTakeOff = (Button) findViewById(R.id.btnTakeOff);
        tvPositionInfoLable = (TextView) findViewById(R.id.tvPositionInfoLable);


        // START -- Receive intent information
        latWorkingPlace = getIntent().getDoubleExtra("latWorkingPlace", -1);
        lngWorkingPlace = getIntent().getDoubleExtra("lngWorkingPlace", -1);
        shiftId = getIntent().getStringExtra("shiftId");
//        Log.w("zf_error", "latWorkingPlace: " + latWorkingPlace + " lngWorkingPlace: " + lngWorkingPlace);
        Log.w("zf_error", "shiftId in ZF_ActivityEmployeeGeoLocation onCreate(): " + shiftId);
        // END -- Receive intent information


        tvGeoInfo = (TextView) findViewById(R.id.tvGeoInfo);

        boolean googleServicesStatus = googleServicesAvailable();
//        Log.i("zf_error", "googleServicesAvailable(): " + googleServicesStatus);

        if (googleServicesStatus) {

            if (this.checkGPSStatus()) {
                initMap();
            }

        } else {

            Log.w("fan_error", "Google service is not available now.");
        }

        btnTakeOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // When click "Take off" button, send OK back to parent activity
                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                finish();

            }
        });

    }

    private boolean checkGPSStatus() {

        boolean gps_enabled = false;

        LocationManager mLocationManager = (LocationManager) ZF_ActivityEmployeeGeoLocation.this.getSystemService(Context.LOCATION_SERVICE);

        try {
            gps_enabled = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
//            Log.w("zf_error", "gps_enabled: " + Boolean.toString(gps_enabled));
        } catch (Exception ex) {
        }


        if (gps_enabled) {
//            Toast.makeText(ZF_ActivityEmployeeGeoLocation.this, "GPS & network are ON", Toast.LENGTH_SHORT).show();
            return true;
        } else {

            if (!gps_enabled) {
//                Toast.makeText(ZF_ActivityEmployeeGeoLocation.this, "GPS is OFF", Toast.LENGTH_SHORT).show();
                this.askUserTurnOnGPS();
            }
            return false;
        }
    }

    private void askUserTurnOnGPS() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(ZF_ActivityEmployeeGeoLocation.this);
        dialog.setMessage("GPS is OFF now.");
        dialog.setPositiveButton("Turn On", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface paramDialogInterface, int paramInt) {

                Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                ZF_ActivityEmployeeGeoLocation.this.startActivity(myIntent);
            }
        });
        dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface paramDialogInterface, int paramInt) {

            }
        });
        dialog.show();
    }

    // This method is for creating overflow actions (menu in action bar)
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.zf_menu_in_activity_employee_geo_locaiton, menu);
        return super.onCreateOptionsMenu(menu);
    }

    // This method is for the operation of overflow actions (menu in action bar)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.aboutProject: {
                Intent intent = new Intent(ZF_ActivityEmployeeGeoLocation.this, ActivityAboutProject.class);
                startActivity(intent);
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    // Check google service in mobile phone. This method is called in onCreate()
    public boolean googleServicesAvailable() {
        GoogleApiAvailability api = GoogleApiAvailability.getInstance();
        int isAvailable = api.isGooglePlayServicesAvailable(this);
        Log.w("zf_error", "isAvailable: " + isAvailable + "");

        if (isAvailable == ConnectionResult.SUCCESS) {

            return true;
        } else if (api.isUserResolvableError(isAvailable)) {
            Dialog dialog = api.getErrorDialog(this, isAvailable, 0);
            dialog.show();

        } else {
            Toast.makeText(this, "Google play services is OFF in your mobile", Toast.LENGTH_LONG).show();
            this.askUserTurnOnGoogleService();
        }
        return false;
    }

    // This method is called in googleServicesAvailable()
    private void askUserTurnOnGoogleService() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(ZF_ActivityEmployeeGeoLocation.this);
        dialog.setMessage("Google play service is OFF now.");
        dialog.setPositiveButton("Turn On", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface paramDialogInterface, int paramInt) {

                Intent myIntent = new Intent(Settings.ACTION_MANAGE_APPLICATIONS_SETTINGS);
                ZF_ActivityEmployeeGeoLocation.this.startActivity(myIntent);
            }
        });
        dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface paramDialogInterface, int paramInt) {

            }
        });
        dialog.show();
    }

    // This method is called in onCreate()
    // getMapAsync will call onMapReady() in this class.
    private void initMap() {
        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.mapFragment);
        mapFragment.getMapAsync(ZF_ActivityEmployeeGeoLocation.this);
    }

    //
    @Override
    public void onMapReady(GoogleMap googleMap) {
        // googleMap is result of map async from google maps
        mGoogleMap = googleMap;

        if (mGoogleMap != null) {

            // Check permission.
            // Since API 23 (marshmallow), permission can be checked after app gets installed.
            // Before, every required permission is checked during app installation and cannot be changed later.
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.INTERNET}, 99);
                }
                return;
            }
            // This is the get the real current geo location and go to that place
//            mGoogleMap.setMyLocationEnabled(true);


            // Below is to get real time Geo location every 2 seconds
            // Below has to be implement interface GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener
            mGoogleApiClient = new GoogleApiClient.Builder(ZF_ActivityEmployeeGeoLocation.this)
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(ZF_ActivityEmployeeGeoLocation.this)
                    .addOnConnectionFailedListener(ZF_ActivityEmployeeGeoLocation.this)
                    .build();
            mGoogleApiClient.connect();

            // Map will go to the working place center. lat and lng is from parent intent
            this.goToLocationZoom(latWorkingPlace, lngWorkingPlace, zoom);

            // Remove duplicated circle every time when drawing a new circle on the map
            if (workingMovementRange != null) {
                workingMovementRange.remove();
            }
            // Draw circle
            if (latWorkingPlace != -1 && lngWorkingPlace != -1) {
                workingMovementRange = this.drawCircle(new LatLng(latWorkingPlace, lngWorkingPlace));
            }

        } else {
            Toast.makeText(this, "Google map is null", Toast.LENGTH_SHORT).show();
        }


    }

    // This method is for requestPermissions function in onMapReady()
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode) {
            case 99: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    return;
                }
            }
        }
    }

    // This method is called in onMapReady()
    private void goToLocationZoom(double lat, double lng, float zoom) {

        LatLng ll = new LatLng(lat, lng);
        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(ll, zoom);
        mGoogleMap.moveCamera(update);

        MarkerOptions options = new MarkerOptions().position(ll).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE));
        mGoogleMap.addMarker(options);

    }

    // onConnected() is for implementation of interface ConnectionCallbacks
    @Override
    public void onConnected(@Nullable Bundle bundle) {

        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(gpsUpdateInterval);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.INTERNET}, 99);
            }
            return;
        }
        // Every time in order to get the location update, I have to use listener.
        // So it has to implement interface com.google.android.gms.location.LocationListener
        // Then overwrite method onLocationChanged
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }

    // onConnectionSuspended() is for implementation of interface ConnectionCallbacks
    @Override
    public void onConnectionSuspended(int i) {

        Toast.makeText(ZF_ActivityEmployeeGeoLocation.this, "onConnectionSuspended", Toast.LENGTH_SHORT).show();
    }

    // onConnectionFailed() is for implementation of interface OnConnectionFailedListener
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

        Toast.makeText(ZF_ActivityEmployeeGeoLocation.this, "onConnectionFailed", Toast.LENGTH_SHORT).show();
    }


    // onLocationChanged() is for implementation of interface LocationListener
    // (It's from com.google.android.gms.location, not android.location.LocationListener
    @Override
    public void onLocationChanged(Location location) {

        if (location == null) {
            Toast.makeText(ZF_ActivityEmployeeGeoLocation.this, "location is null", Toast.LENGTH_SHORT).show();
        } else {
            LatLng ll = new LatLng(location.getLatitude(), location.getLongitude());
            CameraUpdate update = CameraUpdateFactory.newLatLngZoom(ll, zoom);
            latUserPosition = location.getLatitude();
            lngUserPosition = location.getLongitude();
            double distance = this.calculateDistance(latUserPosition, latWorkingPlace, lngUserPosition, lngWorkingPlace);
//            tvGeoInfo.setText("Latitude: " + location.getLatitude() + "\nLongitude: " + location.getLongitude());


            if (distance < radiusOfMovement) {
                tvGeoInfo.setText("You are in the movement range now.");
            } else {

                tvGeoInfo.setText("The distance between you and your movement range is ");
                tvGeoInfo.append(this.setTextColor(Math.round(Math.abs(distance - radiusOfMovement)) + "", Color.RED));
                tvGeoInfo.append(this.setTextColor(" meters. ", Color.GRAY));
                tvGeoInfo.append(this.setTextColor("Please go back to work.", Color.RED));

                android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(ZF_ActivityEmployeeGeoLocation.this);
                builder.setMessage("Please go back to work").setNegativeButton("I know", null).create().show();

            }

            mGoogleMap.animateCamera(update);

            this.livePositionUpdate(latUserPosition, lngUserPosition);

            // Clear marker when user's location changes everytime
            if (workingPlaceMarker != null) {
                workingPlaceMarker.remove();
            }
            // Add new marker
            MarkerOptions options = new MarkerOptions().title("Your position").position(ll);
            workingPlaceMarker = mGoogleMap.addMarker(options);


        }

    }

    // This method is called in onMapReady()
    private Circle drawCircle(LatLng latLng) {

        CircleOptions circleOptions = new CircleOptions()
                .center(latLng)
                .radius(radiusOfMovement)
                .fillColor(0x33FF0000)
                .strokeColor(Color.RED)
                .strokeWidth(2);
        return mGoogleMap.addCircle(circleOptions);
    }

    // This method is called in onLocationChanged()
    private double calculateDistance(double lat1, double lat2, double lon1, double lon2) {

        final int R = 6371; // Radius of the earth

        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c * 1000; // convert to meters


        distance = Math.pow(distance, 2);

        return Math.sqrt(distance);
    }

    // This method is called in onLocationChanged()
    private CharSequence setTextColor(String str, int color) {
        final SpannableStringBuilder sb = new SpannableStringBuilder(str);

        // Span to set text color to some RGB value
        final ForegroundColorSpan fcs = new ForegroundColorSpan(color);

        // Span to make text bold
//        final StyleSpan bss = new StyleSpan(android.graphics.Typeface.BOLD);

        // Set the text color for first 4 characters
        sb.setSpan(fcs, 0, str.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);

        // make them also bold
//        sb.setSpan(bss, 0, str.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);

        return sb;
    }

    private void livePositionUpdate(double latUserPosition, double lngUserPosition) {

        // Use volley to communicate with php web server
        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    boolean success = jsonResponse.getBoolean("success");
                    Log.i("zf_error", "success in livePositionUpdate()" + Boolean.toString(success));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        };

        ZF_RequestLivePositionUpdate livePositionUpdateRequestZF = new ZF_RequestLivePositionUpdate(shiftId, Double.toString(latUserPosition), Double.toString(lngUserPosition), responseListener);
        RequestQueue queue = Volley.newRequestQueue(ZF_ActivityEmployeeGeoLocation.this);
        queue.add(livePositionUpdateRequestZF);
    }
}
