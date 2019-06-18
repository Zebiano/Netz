package com.sihbar.netz;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import org.w3c.dom.Text;

public class SaveNewLink extends AppCompatActivity {

    // Variables
    private static final String TAG = "SaveLink";

    DocumentSnapshot userInfo;
    DocumentSnapshot foundUserInfo;

    // Firebase
    FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save_new_link);

        // Get the Intent that started this activity and extract the string
        Intent intent = getIntent();
        String app = intent.getStringExtra("app");
        String handle = intent.getStringExtra("handle");

        String myLink = app + ".com/" + handle;


        // Sets userInfo
        userInfo = Home.userInfo;

        // Updates Document
        DocumentReference userRef = firebaseFirestore.collection("users").document(userInfo.getId());
        userRef.update("links", FieldValue.arrayUnion(myLink))
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "onSuccess: ");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "onFailure: " + e);
                    }
                });

        finish();
    }
}
