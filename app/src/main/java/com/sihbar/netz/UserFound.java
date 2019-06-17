package com.sihbar.netz;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

public class UserFound extends AppCompatActivity {

    // Variables
    private static final String TAG = "UserFound";

    TextView textViewName;
    TextView textViewWork;
    TextView textViewCountry;
    TextView textViewBio;

    String qrCodeInfo;
    DocumentSnapshot userInfo;

    // Firebase
    FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_found);

        // Variables
        textViewName = findViewById(R.id.textViewName);
        textViewWork = findViewById(R.id.textViewWork);
        textViewCountry = findViewById(R.id.textViewCountry);
        textViewBio = findViewById(R.id.textViewBio);

        loadUserInfo();
    }

    // Loads info to activity
    public void loadUserInfo() {
        Log.d(TAG, "loadUserInfo: ");

        // Get QR code info
        qrCodeInfo = HomeFragment.qrCodeInfo;
        int qrEmailIndexBeg = qrCodeInfo.indexOf("EMAIL:") + 6;
        int qrEmailIndexFin = qrCodeInfo.substring(qrEmailIndexBeg).indexOf(";") + qrEmailIndexBeg;
        String qrEmail = qrCodeInfo.substring(qrEmailIndexBeg, qrEmailIndexFin);
        Log.d(TAG, "loadUserInfo: Beg: " + qrEmailIndexBeg);
        Log.d(TAG, "loadUserInfo: Fin: " + qrEmailIndexFin);
        Log.d(TAG, "loadUserInfo: Res: " + qrCodeInfo.substring(qrEmailIndexBeg, qrEmailIndexFin));

        // Creates a query and then gets it
        Query query = firebaseFirestore.collection("users").whereEqualTo("email", qrEmail);
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    if (task.getResult().isEmpty() == false) {
                        Log.d(TAG, "DocumentSnapshot data: " + task.getResult().getDocuments().get(0));

                        // Sets userInfo
                        userInfo = task.getResult().getDocuments().get(0);
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "onComplete: FAIL - " + task.getException());
                }
            }
        });
    }

    // Adds found user to contacts
    public void addUser(View view) {
        Log.d(TAG, "addUser: " + qrCodeInfo);


    }
}
