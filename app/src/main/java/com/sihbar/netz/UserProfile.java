package com.sihbar.netz;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class UserProfile extends AppCompatActivity {

    // Variables
    private static final String TAG = "UserProfile";

    TextView textViewName;
    TextView textViewWork;
    TextView textViewCountry;
    TextView textViewBio;
    ImageView profilPic;
    String userId;

    DocumentSnapshot foundUserInfo;

    // Arrays
    private ArrayList<String> arrayLinks = new ArrayList<>();
    ArrayList<Integer> arrayLogos = new ArrayList<>();

    // Firebase
    FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        // Variables
        textViewName = findViewById(R.id.textViewName);
        textViewWork = findViewById(R.id.textViewWork);
        textViewCountry = findViewById(R.id.textViewCountry);
        textViewBio = findViewById(R.id.textViewBio);
        profilPic = findViewById(R.id.imageViewPicture);
        userId = getIntent().getStringExtra("userId");

        // Loads User info
        loadUserInfo();
    }

    // Loads info to activity
    public void loadUserInfo() {
        Log.d(TAG, "loadUserInfo: "+ userId);

        DocumentReference docRef = firebaseFirestore.collection("users").document(userId);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());

                        // Sets userInfos
                        foundUserInfo = document;

                        // Sets info on view
                        textViewName.setText(foundUserInfo.getString("name"));
                        textViewWork.setText(foundUserInfo.getString("occupation"));
                        textViewCountry.setText(foundUserInfo.getString("country"));
                        textViewBio.setText(foundUserInfo.getString("bio"));

                        // Loads arrays with user content
                        loadArrays();
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }

    // Loads Arrays with data
    private void loadArrays() {
        // Checks for the links array in userDocument
        if (foundUserInfo.get("links") != null) {
            // Sets Links array
            arrayLinks = (ArrayList<String>) foundUserInfo.get("links");
            Log.d(TAG, "loadArrays: " + foundUserInfo);

            // Sets logos arrays
            for (int i = 0; i < arrayLinks.size(); i++) {
                Log.d(TAG, "laodArrays: " + arrayLinks.get(i));
                if (arrayLinks.get(i).contains("facebook")) {
                    Log.d(TAG, "laodArrays: Facebook!");
                    arrayLogos.add(R.drawable.ic__ionicons_svg_logo_facebook);
                } else if (arrayLinks.get(i).contains("github")) {
                    Log.d(TAG, "laodArrays: Github!");
                    arrayLogos.add(R.drawable.ic__ionicons_svg_logo_github);
                } else if (arrayLinks.get(i).contains("instagram")) {
                    Log.d(TAG, "laodArrays: Instagram!");
                    arrayLogos.add(R.drawable.ic__ionicons_svg_logo_instagram);
                } else if (arrayLinks.get(i).contains("linkedin")) {
                    Log.d(TAG, "laodArrays: LinkedIn!");
                    arrayLogos.add(R.drawable.ic__ionicons_svg_logo_linkedin);
                } else if (arrayLinks.get(i).contains("pinterest")) {
                    Log.d(TAG, "laodArrays: Pinterest!");
                    arrayLogos.add(R.drawable.ic__ionicons_svg_logo_pinterest);
                } else if (arrayLinks.get(i).contains("slack")) {
                    Log.d(TAG, "laodArrays: Slack!");
                    arrayLogos.add(R.drawable.ic__ionicons_svg_logo_slack);
                } else if (arrayLinks.get(i).contains("snapchat")) {
                    Log.d(TAG, "laodArrays: Snapchat!");
                    arrayLogos.add(R.drawable.ic__ionicons_svg_logo_snapchat);
                } else if (arrayLinks.get(i).contains("twitter")) {
                    Log.d(TAG, "laodArrays: Twitter!");
                    arrayLogos.add(R.drawable.ic__ionicons_svg_logo_twitter);
                } else if (arrayLinks.get(i).contains("youtube")) {
                    Log.d(TAG, "laodArrays: Youtube!");
                    arrayLogos.add(R.drawable.ic__ionicons_svg_logo_youtube);
                } else {
                    Log.d(TAG, "laodArrays: Not recognized: " + arrayLinks.get(i));
                    arrayLogos.add(R.drawable.ic__ionicons_svg_md_warning);
                }
            }

            // Initialises the recycler view
            initRecyclerView();
        } else {
            Log.d(TAG, "laodArrays: Null arrayLinks");
            // TODO: Este else acontece se nao haver links!
        }
    }

    // Initialize recyclerView
    private void initRecyclerView () {
        Log.d(TAG, "initRecyclerView: ");

        RecyclerView recyclerView = this.findViewById(R.id.recyclerView);
        RVAdapter_socialLinks_userFound adapter = new RVAdapter_socialLinks_userFound(this, arrayLogos, arrayLinks);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Loads Image
        loadImage(profilPic, foundUserInfo.getString("userId"));
    }

    // Adds image
    public void loadImage(ImageView pic, String userId) {
        // Variables
        Log.d(TAG, "ID: " + userId);

        // Reference to an image file in Cloud Storage
        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        StorageReference profilePicRef = storageReference.child("profilepic/" + userId);

        Log.d(TAG, "setImage: " + profilePicRef);

        // Download directly from StorageReference using Glide
        // (See MyAppGlideModule for Loader registration)
        GlideApp.with(UserProfile.this)
                .load(profilePicRef)
                .into(pic);
    }

    // Removes a user from the contacts
    public void removeUser(View view) {
        Log.d(TAG, "removeUser: ");

        FirebaseFirestore.getInstance().collection("users").document(Home.userInfo.getId()).update("contacts", FieldValue.arrayRemove(userId))
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "onSuccess: Removed link successfully: " + userId);
                        Toast.makeText(UserProfile.this, "User removed from contacts!", Toast.LENGTH_SHORT).show();

                        // Redirect to Home
                        startActivity(new Intent(UserProfile.this, Home.class));
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "onFailure: Failed removing link: " + e);
                    }
                });
    }
}
