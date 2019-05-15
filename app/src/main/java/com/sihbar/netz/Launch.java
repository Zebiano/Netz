package com.sihbar.netz;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class Launch extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);
    }

    // Start Register Activity
    public void startRegisterActivity(View view)
    {
        startActivity(new Intent(this, Register.class));
    }

    // Start Login Activity
    public void startLoginActivity(View view)
    {
        startActivity(new Intent(this, Login.class));
    }
}
