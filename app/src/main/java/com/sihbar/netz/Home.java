package com.sihbar.netz;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

public class Home extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "Home";

    // Variables
    static DocumentSnapshot userInfo;
    DocumentReference userRef;

    // Firebase
    FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_home);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        navView.setOnNavigationItemSelectedListener(this);

        // Gets user data
        getUserData();

        // Load Home Fragment
        loadFragment(new HomeFragment());
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        Fragment fragment = null;

        if (userInfo != null) {
            switch (menuItem.getItemId()) {
                case R.id.navigation_home:
                    fragment = new HomeFragment();
                    break;
                case R.id.navigation_events:
                    fragment = new EventsFragment();
                    break;
                case R.id.navigation_contacts:
                    fragment = new ContactsFragment();
                    break;
                case R.id.navigation_profile:
                    fragment = new ProfileFragment();
                    break;
            }
        } else {
            Toast.makeText(getApplicationContext(), "We're sorry, right now is not the time for that.", Toast.LENGTH_LONG).show();
        }

        return loadFragment(fragment);
    }

    // Gets the users data from the database
    public void getUserData() {
        Log.d(TAG, "getUserData: " + firebaseAuth.getCurrentUser().getUid());

        // Creates a query and then gets it
        Query query = firebaseFirestore.collection("users").whereEqualTo("userId", firebaseAuth.getCurrentUser().getUid());
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    if (task.getResult().isEmpty() == false) {
                        Log.d(TAG, "DocumentSnapshot data: " + task.getResult().getDocuments().get(0));

                        // Updates variables whenever a change happens to the database
                        userRef = firebaseFirestore.collection("users").document(task.getResult().getDocuments().get(0).getId());
                        userRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                            @Override
                            public void onEvent(@Nullable DocumentSnapshot snapshot,
                                                @Nullable FirebaseFirestoreException e) {
                                if (e != null) {
                                    Log.w(TAG, "Listen failed.", e);
                                    return;
                                }

                                if (snapshot != null && snapshot.exists()) {
                                    Log.d(TAG, "Current data: " + snapshot.getData());

                                    // Saves updated user to variable
                                    userInfo = snapshot;
                                } else {
                                    Log.d(TAG, "Current data: null");
                                }
                            }
                        });
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "onComplete: FAIL - " + task.getException());
                }
            }
        });
    }

    // Starts a new fragment
    public boolean loadFragment(Fragment fragment) {
        if (fragment != null) {

            getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, fragment).commit();
            return true;
        }
        return false;
    }
}
