package com.its5314.project.beontime;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

/**
 * Created by Zhongjie FAN on 2017-12-11.
 */

public class ActivityAboutProject extends AppCompatActivity {

    TextView tvAboutProject;
    TextView tvAboutTeam;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_project);

        // START -- Toolbar section
        Toolbar beontimeToolbar = (Toolbar) findViewById(R.id.beontimeToolbar);
        setSupportActionBar(beontimeToolbar);
        getSupportActionBar().setTitle(" BeOnTime");
        beontimeToolbar.setLogo(R.drawable.beontime_logo_32x32);
        beontimeToolbar.setTitleTextColor(Color.WHITE);
        // END -- Toolbar section

        tvAboutProject = (TextView) findViewById(R.id.tvAboutProject);
        tvAboutTeam = (TextView) findViewById(R.id.tvAboutTeam);

        tvAboutProject.setText("Android App Development\nCourse Project @ 2017");
        tvAboutTeam.setText("Prepared By:\n");
        tvAboutTeam.append("Pinesh\n");
        tvAboutTeam.append("Vaishnavi\n");
        tvAboutTeam.append("Dhruvin\n");
        tvAboutTeam.append("Zhongjie");
    }
}
