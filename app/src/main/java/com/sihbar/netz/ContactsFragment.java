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

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class ContactsFragment extends Fragment {

    // Variables
    private static final String TAG = "ContactsFragment";
    DocumentSnapshot userInfo;

    // Arrays
    private ArrayList<String> arrayNames = new ArrayList<>();
    ArrayList<Integer> arrayImages = new ArrayList<>();

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
        Home home = (Home)getActivity();
        userInfo = home.userInfo;
        firebaseFirestore = FirebaseFirestore.getInstance();

        // Load Stuff
        loadArrays(view);

        super.onViewCreated(view, savedInstanceState);
    }

    // Load arrays
    public void loadArrays(View view) {
        Log.d(TAG, "loadArrays: " + userInfo.get("contacts"));
        Object arrayContacts = userInfo.get("contacts");

        // TODO: For do arrayContacts
        /*for (int i = 0; i < arrayContacts.size(); i++) {

        }*/

        //Query getContact = firebaseFirestore.collection("users").document(userInfo.get("contacts"));

        // Initialises the recycler view
        //initRecyclerView(view);
    }

    // Initialize recyclerView
    private void initRecyclerView (View view) {
        Log.d(TAG, "initRecyclerView: ");

        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        RVAdapter_contacts adapter = new RVAdapter_contacts(getActivity(), arrayImages, arrayNames);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    }
}
