package com.sihbar.netz;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    // Start Launch Activity
    public void startLaunchActivity(View view)
    {
        startActivity(new Intent(this, Launch.class));
    }

    public void startHome(View view)
    {
        startActivity(new Intent(this, Home.class));
    }

    public void startTest(View view)
    {
        startActivity(new Intent(this, TestActivity.class));
    }

}
