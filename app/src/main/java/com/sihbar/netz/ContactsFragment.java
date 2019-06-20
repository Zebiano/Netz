package com.sihbar.netz;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class ContactsFragment extends Fragment {

    // Variables
    private static final String TAG = "ContactsFragment";

    // Arrays
    private ArrayList<String> arrayNames = new ArrayList<>();
    ArrayList<Integer> arrayImages = new ArrayList<>();
    List<String> arrayContacts;

    // Firebase
    FirebaseFirestore firebaseFirestore;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_contacts, null);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onViewCreated: ");

        // Variables
        firebaseFirestore = FirebaseFirestore.getInstance();

        // Load Stuff
        loadArrays(view);

        super.onViewCreated(view, savedInstanceState);
    }

    // Load arrays
    public void loadArrays(View view) {
        Log.d(TAG, "loadArrays: " + Home.userInfo.get("contacts"));

        arrayContacts = (List<String>) Home.userInfo.get("contacts");
        if (arrayContacts != null) {
            for (int i = 0; i < arrayContacts.size(); i++) {
                //Log.d(TAG, "loadArrays: " + arrayContacts.get(i));

                // Gets contacts
                getContacts(arrayContacts.get(i), i, view);
            }
        } else {
            Toast.makeText(getActivity(), "You have no Contacts! Consider adding someone :)", Toast.LENGTH_SHORT).show();
        }
    }

    // Gets contacts
    public void getContacts(String document, final int i, final View view) {
        Log.d(TAG, "getContacts: " + document);
        DocumentReference userRef = firebaseFirestore.collection("users").document(document);
        userRef.get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        Log.d(TAG, "onComplete: ");
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                //Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                                //Log.d(TAG, "onComplete: " + document.get("contacts"));
                                arrayNames.add(document.getString("name"));
                                arrayImages.add(R.drawable.ic__ionicons_svg_md_warning);
                                Log.d(TAG, "onComplete: " + arrayImages + ", " + arrayNames);

                                // Checks if its the last loop
                                if (i + 1 == arrayContacts.size()) {
                                    // Initialises the recycler view
                                    initRecyclerView(view);
                                }
                            } else {
                                Log.d(TAG, "No such document");
                            }
                        } else {
                            Log.d(TAG, "get failed with ", task.getException());
                        }
                    }
                });
    }

    // Initialize recyclerView
    private void initRecyclerView(View view) {
        Log.d(TAG, "initRecyclerView: ");

        Log.d(TAG, "initRecyclerView - Images: " + arrayImages + ", Names: " + arrayNames);

        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        RVAdapter_contacts adapter = new RVAdapter_contacts(getActivity(), arrayImages, arrayNames);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    }
}
