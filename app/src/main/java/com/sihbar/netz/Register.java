package com.sihbar.netz;

import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Register extends AppCompatActivity {

    // Variables
    private String TAG = "ActivityRegister"
    private Button buttonRegister;
    private EditText editTextEmail;
    private EditText editTextPassword;
    private ProgressDialog progressDialog;

    // Firebase
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Firebase
        firebaseAuth = FirebaseAuth.getInstance();

        // ProgressBar
        progressDialog = new ProgressDialog(this);

        // Buttons
        buttonRegister = (Button) findViewById(R.id.buttonRegister);

        // EditTexts
        editTextEmail = (EditText) findViewById(R.id.editTextEmail);
        editTextPassword = (EditText) findViewById(R.id.editTextPassword);
    }

    // OnClick of ButtonRegister
    public void clickButtonRegister(View view) {
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        //Log.d(TAG, email);
        //Log.d(TAG, password);

        // If input is empty
        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Please entner a valid email and password", Toast.LENGTH_SHORT).show();
            return;
        } else {
            progressDialog.setMessage("Registering User");
            progressDialog.show();

            firebaseAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            //Log.d(TAG, "onComplete");

                            if (task.isSuccessful()) {
                                Log.d(TAG, "createUserWithEmail:success");
                                progressDialog.cancel();
                                Toast.makeText(Register.this, "Successfully registered User!", Toast.LENGTH_SHORT).show();

                                // Falta adicionar user, ve este link: https://firebase.google.com/docs/auth/android/start?authuser=0

                            } else {
                                Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                progressDialog.cancel();
                                Toast.makeText(Register.this, "Failed registering User!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    // Logs to console. ONLY STRINGS
    private void LogMe(String input) {
        Log.d("ActivityRegister", input);
    }
}
