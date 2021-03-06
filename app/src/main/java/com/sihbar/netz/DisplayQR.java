package com.sihbar.netz;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class DisplayQR extends Fragment {

    private static final String TAG = "DisplayQR";
    ImageView qrcode;
    private ProgressDialog progressDialog;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        // Inflater/View
        View view = inflater.inflate(R.layout.fragment_displayqr, null);

        // Variables
        Button btnBack = view.findViewById(R.id.btnBack);
        qrcode = view.findViewById(R.id.imgQRcode);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            Log.d(TAG, "onCreateView: " + user.getUid());
            showQR(qrcode, user.getUid());
        } else {

        }

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Launch profile fragment
                Fragment HomeFragment = new HomeFragment();
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.fragmentContainer, HomeFragment);
                transaction.addToBackStack(null);  // if written, this transaction will be added to backstack
                transaction.commit();
            }
        });



        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // ProgressDialog
        progressDialog = new ProgressDialog(getActivity());
    }

    // Shows QR Code
    public void showQR(ImageView qrcode, String userID) {
        progressDialog.setMessage("Loading QR Code...");
        progressDialog.show();

        // Reference to an image file in Cloud Storage
        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        StorageReference qrCodesRef = storageReference.child("qrcodes/" + userID);
        StorageReference qrReferece = storageReference.child("qrcodes/" + userID + ".jpeg");

        Log.d(TAG, "showQR: " + qrReferece);


        // Download directly from StorageReference using Glide
        // (See MyAppGlideModule for Loader registration)
        GlideApp.with(DisplayQR.this/* context */)
                .load(qrCodesRef)
                .into(qrcode);

        progressDialog.cancel();
    }
}
