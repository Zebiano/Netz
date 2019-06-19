package com.sihbar.netz;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.zxing.WriterException;

import java.io.ByteArrayOutputStream;
import java.io.File;

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
    private EditText editTextOccupation;
    private ProgressDialog progressDialog;

    // Firebase
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firestore;
    private FirebaseStorage firebaseStorage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Firebase
        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();

        // ProgressBar
        progressDialog = new ProgressDialog(this);

        // EditTexts
        editTextName = findViewById(R.id.editTextName);
        editTextPhone = findViewById(R.id.editTextPhone);
        editTextPassword = findViewById(R.id.editTextPassword);
        editTextPasswordR = findViewById(R.id.editTextPasswordR);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextCountry = findViewById(R.id.editTextCountry);
        editTextOccupation = findViewById(R.id.editTextOccupation);
    }

    // OnClick of ButtonRegister
    public void clickButtonRegister(View view) {
        String name = editTextName.getText().toString().trim();
        String phone = editTextPhone.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        String passwordR = editTextPasswordR.getText().toString().trim();
        String email = editTextEmail.getText().toString().trim();
        String country = editTextCountry.getText().toString().trim();
        String occupation = editTextOccupation.getText().toString().trim();
        //Log.d(TAG, "clickButtonRegister: " + name + ", " + phone + ", " + password + ", " + passwordR + ", " + email + ", " + country + ", " + occupation + ".");

        // If input is empty
        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(phone) || TextUtils.isEmpty(password) || TextUtils.isEmpty(passwordR) || TextUtils.isEmpty(email) || TextUtils.isEmpty(country)) {
            Toast.makeText(this, "Please enter valid values!", Toast.LENGTH_LONG).show();
            return;
        } else if (!password.equals(passwordR)) {
            Toast.makeText(this, "The passwords don't match!", Toast.LENGTH_LONG).show();
            return;
        } else if (password.length() < 6) {
            Toast.makeText(this, "The password has to be at least 6 characters long!", Toast.LENGTH_LONG).show();
            return;
        } else {
            progressDialog.setMessage("Registering User...");
            progressDialog.show();

            // Create a user on the Firebase Auth and if successful saves to databse
            createAuthUser(name, phone, password, email, country, occupation);
        }
    }

    // Creates a new Auth user in the firebase
    public void createAuthUser(final String name, final String phone, final String password, final String email, final String country, final String occupation) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        //Log.d(TAG, "onComplete");

                        if (task.isSuccessful()) {
                            Log.d(TAG, "createUserWithEmail:success");

                            // Saves user to firebase
                            saveUserToFirebase(name, phone, password, email, country, occupation);
                        } else {
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            progressDialog.cancel();
                            Toast.makeText(Register.this, "" + task.getException().getMessage(), Toast.LENGTH_LONG).show(); // TODO: Create this on all exceptions so the user knows. Maybe not on all, but on some others...
                        }
                    }
                });
    }

    // Saves the user info onto the firebase database
    public void saveUserToFirebase(final String name, final String phone, final String password, final String email, final String country, final String occupation) {
        final String userId = firebaseAuth.getUid();

        // TODO: Add links to user
        // Create new User with our own class
        final User user = new User(
                userId,
                name,
                phone,
                password,
                email,
                country,
                occupation
        );

        // Save User to Firestore
        firestore.collection("users").add(user)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "onSuccess: Save user to firestore");

                        // Generate a qr code for this user and save it onto the firebase storage
                        generateQrCode(user);

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
    }

    // Generates and saves a QR code to phones cache
    public void generateQrCode(User user) {
        Log.d(TAG, "generateQrCode: ");

        // TODO: Quando um user atualizar a informacao dele no perfil, o qr code que ta na storage vai ter que atualizar tmb
        // Generates a new qr code
        QRGEncoder qrgEncoder = new QRGEncoder(
                "MECARD:N:" + user.getName() + ";" +
                "ORG:" + user.getOccupation() + ";" +
                "TEL:" + user.getPhone() + ";" +
                "EMAIL:" + user.getEmail() + ";" +
                "ADR:" + user.getCountry() + ";",
                null,
                QRGContents.Type.TEXT,
                810
        );
        try {
            // Getting QR-Code as Bitmap
            Bitmap bitmap = qrgEncoder.encodeAsBitmap();
            try {
                boolean save = QRGSaver.save(
                        this.getCacheDir() + "/",
                        "qrcode",
                        bitmap,
                        QRGContents.ImageType.IMAGE_JPEG
                );
                String result = save ? "Image Saved" : "Image Not Saved";
                Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();
                Log.d(TAG, "generateQrCode: " + result);

                // Saves qr code onto firebase storage
                saveQrCode(user.getUserId());
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (WriterException e) {
            Log.v(TAG, e.toString());
        }
    }

    // Saves QR Code onto the firebase storage
    public void saveQrCode(final String userId) {
        Log.d(TAG, "saveQrCode: ");
        StorageReference qrCodesRef = firebaseStorage.getReference().child("qrcodes/" + userId);
        final File qrcode = new File(this.getCacheDir() + "/qrcode.jpg");
        final Uri uri = Uri.fromFile(qrcode);
        UploadTask uploadTask = qrCodesRef.putFile(uri);

        // Register observers to listen for when the upload is done or if it fails
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Log.d(TAG, "onSuccess: QrCode Saved to storage! " + taskSnapshot);

                // Delete cache with qr code
                qrcode.delete();

                // Saves Profile Picture for the user (has to be in callback, else itll never save)
                saveProfile(userId);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
                // TODO: In case qr code generation fails, say the registration failed. De certa forma tamos em callback hell porcausa do try catch...
                Log.d(TAG, "onFailure: Fuck. Rip QR code saving in storage.");
            }
        });
    }

    // Saves the profile picture of the user
    public void saveProfile(String userId){
        Bitmap bitmap = BitmapFactory.decodeResource( getResources(), R.drawable.logo);
        StorageReference profilePicRef = firebaseStorage.getReference().child("profilepic/" + userId);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = profilePicRef.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
                Log.w(TAG, "onFailure: Rip saving picture to storage", exception);
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                Log.d(TAG, "onSuccess: Save Pic to storage!");
            }
        });

    }

    public void goBack(View view) {
        startActivity(new Intent(this, MainActivity.class));
    }
}
