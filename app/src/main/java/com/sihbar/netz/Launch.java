package com.sihbar.netz;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class Launch extends AppCompatActivity {

    // Variables
    private static final String TAG = "Launch";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    // Start Register Activity
    public void startRegisterActivity(View view) {
        startActivity(new Intent(this, Register.class));
    }

    // Start Login Activity
    public void startLoginActivity(View view) {
        startActivity(new Intent(this, Login.class));
    }

    // Start Home Activity
    public void startHomeActivity(View view) {
        startActivity(new Intent(this, Home.class));
    }
}
