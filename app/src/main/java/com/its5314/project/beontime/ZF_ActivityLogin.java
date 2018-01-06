package com.its5314.project.beontime;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class ZF_ActivityLogin extends AppCompatActivity {

    EditText etUsername;
    EditText etPassword;
    Button btnLogin;

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.zf_activity_login);

        // START -- Toolbar section
        Toolbar beontimeToolbar = (Toolbar) findViewById(R.id.beontimeToolbar);
        setSupportActionBar(beontimeToolbar);
        getSupportActionBar().setTitle(" BeOnTime Mobile");
        beontimeToolbar.setLogo(R.drawable.beontime_logo_32x32);
        beontimeToolbar.setTitleTextColor(Color.WHITE);
        // END -- Toolbar section

        sharedPreferences = getSharedPreferences("BE_ON_TIME",MODE_PRIVATE);
        editor = sharedPreferences.edit();

        etUsername = (EditText) findViewById(R.id.etUsername);
        etPassword = (EditText) findViewById(R.id.etPassword);
        btnLogin = (Button) findViewById(R.id.btnLogin);

        // Clear password field in onCreate stage
        etPassword.setText("");

        btnLogin.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                String username = etUsername.getText().toString();
                String password = etPassword.getText().toString();

//                Log.i("zf_error", username + " " + password);

                // Use volley to communicate with php web server
                Response.Listener<String> responseListener = new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            boolean success = jsonResponse.getBoolean("success");
//                            Log.i("zf_error", Boolean.toString(success));
                            if (success) {

                                String firstName = jsonResponse.getString("firstName");
                                String lastName = jsonResponse.getString("lastName");
                                String userId = jsonResponse.getString("userId");
                                String roleId = jsonResponse.getString("roleId");
                                String companyId = jsonResponse.getString("CompanyId");
//                                Log.w("zf_error", roleId + "");

                                editor.putString("USER_ID",userId);
                                editor.putString("ROLE_ID",jsonResponse.getString("roleId"));
                                editor.putString("COMPANY_ID",jsonResponse.getString("CompanyId"));
                                editor.apply();

//                                Log.w("zf_error", roleId + "");

                                switch (Integer.parseInt(roleId)) {
                                    // 10 means manager role
                                    case 10: {

                                        break;
                                    }
                                    // 11 means client role
                                    case 11: {

                                        Intent intent = new Intent(ZF_ActivityLogin.this, ZF_ActivityEmployeeStartWork.class);
                                        intent.putExtra("firstName", firstName);
                                        intent.putExtra("lastName", lastName);
                                        intent.putExtra("userId", userId);
                                        intent.putExtra("roleId", roleId);
                                        intent.putExtra("companyId",companyId);
                                        ZF_ActivityLogin.this.startActivity(intent);
                                        finish();

                                        break;
                                    }
                                    // 12 means employee role
                                    case 12: {

                                        // Pass info to activity ZF_ActivityEmployeeStartWork
                                        Intent intent = new Intent(ZF_ActivityLogin.this, ZF_ActivityEmployeeStartWork.class);
                                        intent.putExtra("firstName", firstName);
                                        intent.putExtra("lastName", lastName);
                                        intent.putExtra("userId", userId);
                                        intent.putExtra("roleId", roleId);
                                        intent.putExtra("companyId",companyId);
                                        ZF_ActivityLogin.this.startActivity(intent);
                                        finish();

                                        break;
                                    }

                                }


                            } else {

                                AlertDialog.Builder builder = new AlertDialog.Builder(ZF_ActivityLogin.this);
                                builder.setMessage("Login Failed").setNegativeButton("Retry", null).create().show();

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                };

                ZF_RequestLogin loginRequestZF = new ZF_RequestLogin(username, password, responseListener);
                RequestQueue queue = Volley.newRequestQueue(ZF_ActivityLogin.this);
                queue.add(loginRequestZF);
            }
        });

    }

    // Disable back button
    @Override
    public void onBackPressed() {

    }
}
