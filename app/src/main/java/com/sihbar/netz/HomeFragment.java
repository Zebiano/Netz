package com.sihbar.netz;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.graphics.Point;
import android.graphics.Rect;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.camerakit.CameraKitView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcode;
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcodeDetector;
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcodeDetectorOptions;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.common.FirebaseVisionImageMetadata;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import static android.content.Context.CAMERA_SERVICE;

public class HomeFragment extends Fragment {

    // Variables
    private static final String TAG = "HomeFragment";

    // CameraKit
    private CameraKitView cameraKitView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        // Inflater/View
        View view = inflater.inflate(R.layout.fragment_home, null);

        // Buttons
        Button btnTakePicture = view.findViewById(R.id.btnTakePicture);
        Button btnChangeMode = view.findViewById(R.id.btnChangeMode);

        // OnClick Button Take Picture
        btnTakePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: btnTakePicture");

                cameraKitView.captureImage(new CameraKitView.ImageCallback() {
                    @Override
                    public void onImage(CameraKitView cameraKitView, final byte[] capturedImage) {
                        Log.d(TAG, "onImage: CapturedImage: " + capturedImage);

                        // Save image to memory and read it for qr codes
                        readQrImage(capturedImage);
                    }
                });
            }
        });

        // OnClick Button Change Mode
        btnChangeMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: btnChangeMode");
            }
        });

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        cameraKitView = view.findViewById(R.id.camera);
    }

    // FIXME: Camera stops if you tab out of the app and then back in.
    @Override
    public void onStart() {
        Log.d(TAG, "onStart: ");
        super.onStart();
        cameraKitView.onStart();
    }

    @Override
    public void onStop() {
        Log.d(TAG, "onStop: ");
        cameraKitView.onStop();
        super.onStop();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        cameraKitView.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    // Reads QR code by saving an image to the cache, then analyzing it and then deleting it
    public void readQrImage(final byte[] capturedImage) {
        File savedPhoto = new File(getActivity().getCacheDir(), "photo.jpg");
        try {
            Log.d(TAG, "readQrImage: ");
            FileOutputStream outputStream = new FileOutputStream(savedPhoto.getPath());
            outputStream.write(capturedImage);
            outputStream.close();

            try {
                // Get QR code results
                getQrResults(FirebaseVisionImage.fromFilePath(getActivity(), Uri.fromFile(savedPhoto)));
                savedPhoto.delete();
            } catch (IOException e) {
                e.printStackTrace();
                Log.w(TAG, "readQrImage: ", e);
            }
        } catch (java.io.IOException e) {
            e.printStackTrace();
            Log.w(TAG, "readQrImage: ", e);
        }
    }

    public void getQrResults(FirebaseVisionImage image) {
        Log.d(TAG, "getQrResults: ");

        // Qr code options
        FirebaseVisionBarcodeDetectorOptions barcodeOptions =
                new FirebaseVisionBarcodeDetectorOptions.Builder()
                        .setBarcodeFormats(
                                FirebaseVisionBarcode.FORMAT_QR_CODE)
                        .build();

        // Set detector with barcodeOptions
        FirebaseVisionBarcodeDetector detector = FirebaseVision.getInstance()
                //.getVisionBarcodeDetector();
                .getVisionBarcodeDetector(barcodeOptions);

        Task<List<FirebaseVisionBarcode>> result = detector.detectInImage(image)
                .addOnSuccessListener(new OnSuccessListener<List<FirebaseVisionBarcode>>() {
                    @Override
                    public void onSuccess(List<FirebaseVisionBarcode> barcodes) {
                        // Task completed successfully
                        Log.d(TAG, "onSuccess: " + barcodes);

                        for (FirebaseVisionBarcode barcode : barcodes) {
                            Rect bounds = barcode.getBoundingBox();
                            Point[] corners = barcode.getCornerPoints();

                            String rawValue = barcode.getRawValue();

                            int valueType = barcode.getValueType();
                            // See API reference for complete list of supported types
                            Log.d(TAG, "onSuccess: " + valueType);
                            Log.d(TAG, "onSuccess: " + rawValue);

                            // TODO: Open an activity that shows the profile o another user! Not a fragment... Makes things easier.
                            // Launch profile fragment
                            Fragment profileFragment= new ProfileFragment();
                            FragmentTransaction transaction = getFragmentManager().beginTransaction();
                            transaction.replace(R.id.fragment_container, profileFragment);
                            transaction.addToBackStack(null);  // if written, this transaction will be added to backstack
                            transaction.commit();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Task failed with an exception
                        Log.w(TAG, "onFailure: ", e);
                    }
                });
    }
}
