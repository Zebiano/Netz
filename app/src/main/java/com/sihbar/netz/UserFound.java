package com.sihbar.netz;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class UserFound extends AppCompatActivity {

    // Variables
    private static final String TAG = "UserFound";

    // TODO: Add loading screenssssss

    TextView textViewName;
    TextView textViewWork;
    TextView textViewCountry;
    TextView textViewBio;
    ImageView profilPic;

    String qrCodeInfo;
    DocumentSnapshot userInfo;
    DocumentSnapshot foundUserInfo;

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
        profilPic = findViewById(R.id.imageViewPicture);

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
        Query getByEmail = firebaseFirestore.collection("users").whereEqualTo("email", qrEmail);
        getByEmail.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    if (task.getResult().isEmpty() == false) {
                        Log.d(TAG, "DocumentSnapshot data: " + task.getResult().getDocuments().get(0));

                        // Sets userInfos
                        foundUserInfo = task.getResult().getDocuments().get(0);

                        // Sets info on view
                        textViewName.setText(foundUserInfo.getString("name"));
                        textViewWork.setText(foundUserInfo.getString("occupation"));
                        textViewCountry.setText(foundUserInfo.getString("country"));
                        textViewBio.setText(foundUserInfo.getString("bio"));

                        loadImage(profilPic, foundUserInfo.getString("userId"));

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
        Log.d(TAG, "addUser: " + Home.userInfo.getId());

        // Sets userInfo
        userInfo = Home.userInfo;

        // Updates Document
        DocumentReference userRef = firebaseFirestore.collection("users").document(userInfo.getId());
        userRef.update("contacts", FieldValue.arrayUnion(foundUserInfo.getId()))
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
    }

    public void loadImage(ImageView pic, String userId) {

        // Variables

        userInfo = Home.userInfo;
        //String userID = userInfo.getString("userId");
        Log.d(TAG, "ID: " + userId);

        // Reference to an image file in Cloud Storage
        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        StorageReference profilePicRef = storageReference.child("profilepic/" + userId);

        Log.d(TAG, "setImage: " + profilePicRef);


        // Download directly from StorageReference using Glide
        // (See MyAppGlideModule for Loader registration)
        GlideApp.with(UserFound.this)
                .load(profilePicRef)
                .into(pic);

    }
}
