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

    public void openRegisterActivity(View view)
    {
        Intent intent = new Intent(this, Register.class);
        startActivity(intent);
    }
}
