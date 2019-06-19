package com.sihbar.netz;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class ContactsFragment extends Fragment {

    // Variables
    private static final String TAG = "ContactsFragment";
    DocumentSnapshot userInfo;
    private SectionsPageAdapter sectionsPagerAdapter;
    private ViewPager viewPager;

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

        // Sets pageAdapter
        sectionsPagerAdapter = new SectionsPageAdapter(getFragmentManager());

        // Sets up viewPager
        viewPager = (ViewPager) view.findViewById(R.id.container);
        setupViewPager(viewPager);

        TabLayout tabLayout = (TabLayout) view.findViewById(R.id.tabLayout);
        tabLayout.setupWithViewPager(viewPager);

        // Variables
        Home home = (Home) getActivity();
        userInfo = home.userInfo;
        firebaseFirestore = FirebaseFirestore.getInstance();

        // Load Stuff
        //loadArrays(view);

        super.onViewCreated(view, savedInstanceState);
    }

    private void setupViewPager(ViewPager viewPager) {
        SectionsPageAdapter adapter = new SectionsPageAdapter(getFragmentManager());
        adapter.addFragment(new TabContactsRecent(), "Recent");
        adapter.addFragment(new TabContactsFavourites(), "Favourites");
        adapter.addFragment(new TabContacts(), "Contacts");
        viewPager.setAdapter(adapter);
    }

    // Load arrays
    public void loadArrays(View view) {
        //Log.d(TAG, "loadArrays: " + userInfo.getData().get("contacts"));
        // TODO: fazer so se o array existir
        final List<String> arrayContacts = (List<String>) userInfo.get("contacts");

        /*for (int i = 0; i < arrayContacts.size(); i++) {
            Log.d(TAG, "loadArrays: " + arrayContacts.get(i));

            DocumentReference userRef = firebaseFirestore.collection("users").document(arrayContacts.get(i));
            userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            //Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                            //Log.d(TAG, "onComplete: " + document.get("contacts"));
                            arrayNames.add(document.getString("name"));
                            arrayImages.add(5);
                            Log.d(TAG, "onComplete: " + arrayImages + ", " + arrayNames);
                        } else {
                            Log.d(TAG, "No such document");
                        }
                    } else {
                        Log.d(TAG, "get failed with ", task.getException());
                    }
                }
            });

            Log.d(TAG, "loadArrays: " + i + ", " + arrayContacts.size());

            // Checks if its the last loop
            if (i + 1 == arrayContacts.size()) {
                // Initialises the recycler view
                //initRecyclerView(view);
            }
        }*/
    }

    // Initialize recyclerView
    private void initRecyclerView(View view) {
        Log.d(TAG, "initRecyclerView: ");

        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        RVAdapter_contacts adapter = new RVAdapter_contacts(getActivity(), arrayImages, arrayNames);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    }
}
