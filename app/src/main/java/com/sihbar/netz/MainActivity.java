package com.sihbar.netz;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    // Firebase
    public FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Firebase
        firebaseAuth = FirebaseAuth.getInstance();
    }

    // Start Launch Activity
    public void startLaunchActivity(View view) {
        startActivity(new Intent(this, Launch.class));
    }

    // Starts home after attempting login
    public void startHome(View view) {

        // Variables
        String email = "admin@admin.com";
        String password = "admin123";

        // Sign in on firebase Auth
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "signInWithEmail:success");
                            Toast.makeText(MainActivity.this, "Admin logged in!", Toast.LENGTH_SHORT).show();

                            // Redirect to Home
                            startActivity(new Intent(MainActivity.this, Home.class));
                        } else {
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(MainActivity.this, "LogIn failed!", Toast.LENGTH_SHORT).show();

                            // Redirect to Home
                            startActivity(new Intent(MainActivity.this, Home.class));
                        }
                    }
                });
    }

}
