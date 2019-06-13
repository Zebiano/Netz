package com.sihbar.netz;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.core.Query;

import java.util.ArrayList;
import java.util.List;

public class ProfileFragment extends Fragment {

    // Variables
    private static final String TAG = "ProfileFragment";
    DocumentSnapshot userDocument;

    // Arrays
    private ArrayList<String> arrayLinks = new ArrayList<>();
    private ArrayList<String> arrayLogos = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, null);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        // Variables
        Home home = (Home)getActivity();
        userDocument = home.userDocument;

        // Loads arrays with user content
        laodArrays(view);

        super.onViewCreated(view, savedInstanceState);
    }

    // Loads Arrays with data
    private void laodArrays(View view) {
        Log.d(TAG, "laodArrays: " + userDocument.getData());

        // Sets Links array
        arrayLinks = (ArrayList<String>) userDocument.get("links");

        initRecyclerView(view);
    }

    // Initialize recyclerView
    private void initRecyclerView (View view) {
        Log.d(TAG, "initRecyclerView: ");

        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        RVAdapter_socialLinks adapter = new RVAdapter_socialLinks(this, ) // TODO: https://www.youtube.com/watch?v=Vyqz_-sJGFk 19:54

    }
}
