package com.sihbar.netz;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;

public class ProfileFragment extends Fragment {

    // Variables
    private static final String TAG = "ProfileFragment";
    DocumentSnapshot userInfo;

    TextView textViewName;
    TextView textViewWork;
    TextView textViewCountry;
    TextView textViewBio;

    // Arrays
    private ArrayList<String> arrayLinks = new ArrayList<>();
    ArrayList<Integer> arrayLogos = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflater/View
        View view = inflater.inflate(R.layout.fragment_profile, null);

        // Buttons
        Button btnAddLink = view.findViewById(R.id.btnAddLink);

        btnAddLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: btnChangeMode");


                // Launch profile fragment
                Fragment AddNewFragment = new AddNewFragment();
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container , AddNewFragment);
                transaction.addToBackStack(null);  // if written, this transaction will be added to backstack
                transaction.commit();
            }
        });

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        // Variables
        Home home = (Home)getActivity();
        userInfo = home.userInfo;
        textViewName = view.findViewById(R.id.textViewName);
        textViewWork = view.findViewById(R.id.textViewWork);
        textViewCountry = view.findViewById(R.id.textViewCountry);
        textViewBio = view.findViewById(R.id.textViewBio);

        // Load user info
        loadUserInfo(view);

        super.onViewCreated(view, savedInstanceState);
    }

    // Loads user info
    public void loadUserInfo(View view) {

        Log.d(TAG, "loadUserInfo: " + userInfo.getData());

        // Set user info on profile
        textViewName.setText(userInfo.getString("name"));
        textViewWork.setText(userInfo.getString("occupation"));
        textViewCountry.setText(userInfo.getString("country"));
        textViewBio.setText(userInfo.getString("bio"));

        // Loads arrays with user content
        laodArrays(view);
    }

    // Loads Arrays with data
    private void laodArrays(View view) {

        // Checks for the links array in userDocument
        if (userInfo.get("links") != null) {
            // Sets Links array
            arrayLinks = (ArrayList<String>) userInfo.get("links");

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
            initRecyclerView(view);
        } else {
            Log.d(TAG, "laodArrays: Null arrayLinks");
            // TODO: HUGO - Este else acontece se nao haver links!
        }
    }

    // Initialize recyclerView
    private void initRecyclerView (View view) {
        Log.d(TAG, "initRecyclerView: ");

        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        RVAdapter_socialLinks adapter = new RVAdapter_socialLinks(getActivity(), arrayLogos, arrayLinks);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    }
}
