package com.sihbar.netz;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;

public class Launch extends AppCompatActivity {

    // Variables
    private String TAG = "ActivityMainActivity";

    // Firebase
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);
    }

    @Override
    protected void onStart() {
        super.onStart();

        // TODO (crashes app and idk why) Check if user is signed in
        if (firebaseAuth.getCurrentUser() != null) {
            //startActivity(new Intent(this, Home.class));
            Log.d(TAG, "boas");
        }
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
        //startActivity(new Intent(this, Home.class));
    }
}
