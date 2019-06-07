package com.sihbar.netz;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.zxing.WriterException;
import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;
import androidmads.library.qrgenearator.QRGSaver;

public class Register extends AppCompatActivity {

    // Variables
    private static final String TAG = "Register";
    private EditText editTextName;
    private EditText editTextPhone;
    private EditText editTextPassword;
    private EditText editTextPasswordR;
    private EditText editTextEmail;
    private EditText editTextCountry;
    private ProgressDialog progressDialog;

    // Firebase
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Firebase
        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        // ProgressBar
        progressDialog = new ProgressDialog(this);

        // EditTexts
        editTextName = findViewById(R.id.editTextName);
        editTextPhone =  findViewById(R.id.editTextPhone);
        editTextPassword =  findViewById(R.id.editTextPassword);
        editTextPasswordR =  findViewById(R.id.editTextPasswordR);
        editTextEmail =  findViewById(R.id.editTextEmail);
        editTextCountry =  findViewById(R.id.editTextCountry);
    }

    // OnClick of ButtonRegister
    public void clickButtonRegister(View view) {
        final String name = editTextName.getText().toString().trim();
        final String phone = editTextPhone.getText().toString().trim();
        final String password = editTextPassword.getText().toString().trim();
        final String passwordR = editTextPasswordR.getText().toString().trim();
        final String email = editTextEmail.getText().toString().trim();
        final String country = editTextCountry.getText().toString().trim();
        //Log.d(TAG, name);
        //Log.d(TAG, phone);
        //Log.d(TAG, password);
        //Log.d(TAG, passwordR);
        //Log.d(TAG, email);
        //Log.d(TAG, country);

        // If input is empty
        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(phone) || TextUtils.isEmpty(password) || TextUtils.isEmpty(passwordR) || TextUtils.isEmpty(email) || TextUtils.isEmpty(country)) {
            Toast.makeText(this, "Please enter valid values!", Toast.LENGTH_LONG).show();
            return;
        } else if (!password.equals(passwordR)) {
            Toast.makeText(this, "The passwords don't match!", Toast.LENGTH_LONG).show();
            return;
        } else {
            progressDialog.setMessage("Registering User...");
            progressDialog.show();

            // Generate qr code for this user
            generateQrCode();

            Log.d(TAG, "onComplete: " + firebaseAuth.getCurrentUser());

            // Create a user on the Firebase Auth
            firebaseAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            //Log.d(TAG, "onComplete");

                            if (task.isSuccessful()) {
                                Log.d(TAG, "createUserWithEmail:success");

                                String userId = firebaseAuth.getUid();

                                // Create new User with our own class
                                User user = new User(userId, name, phone, password, email, country);
                                // Save User to Firestore
                                firestore.collection("users").add(user)
                                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                            @Override
                                            public void onSuccess(DocumentReference documentReference) {
                                                Log.d(TAG, "addUser:success");

                                                progressDialog.cancel();
                                                Toast.makeText(Register.this, "Successfully registered User!", Toast.LENGTH_SHORT).show();

                                                // Redirect to login
                                                startActivity(new Intent(Register.this, Login.class));
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception error) {
                                                Log.d(TAG, "addUser:failure", error);
                                                progressDialog.cancel();
                                            }
                                        });
                            } else {
                                Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                progressDialog.cancel();
                                Toast.makeText(Register.this, "" + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }
                    });
        }
    }

    // Generates and saves a QR code to phone
    public void generateQrCode() {
        Log.d(TAG, "generateQrCode: ");

        // Initializing the QR Encoder with your value to be encoded, type you required and Dimension
        QRGEncoder qrgEncoder = new QRGEncoder("oh ye", null, QRGContents.Type.TEXT, 810);
        try {
            // Getting QR-Code as Bitmap
            Bitmap bitmap = qrgEncoder.encodeAsBitmap();
            try {
                boolean save = QRGSaver.save(
                        Environment.getExternalStorageDirectory().getPath() + "/QRCode/",
                        "Test",
                        bitmap,
                        QRGContents.ImageType.IMAGE_JPEG
                );
                String result = save ? "Image Saved" : "Image Not Saved";
                Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (WriterException e) {
            Log.v(TAG, e.toString());
        }
    }
}
