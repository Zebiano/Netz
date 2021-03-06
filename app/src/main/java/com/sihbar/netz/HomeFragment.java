package com.sihbar.netz;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
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
import android.widget.Toast;

import com.camerakit.CameraKitView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcode;
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcodeDetector;
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcodeDetectorOptions;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class HomeFragment extends Fragment {

    // Variables
    private static final String TAG = "HomeFragment";

    // CameraKit
    private CameraKitView cameraKitView;

    public static String qrCodeInfo;
    private ProgressDialog progressDialog;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        // Inflater/View
        View view = inflater.inflate(R.layout.fragment_home, null);

        // Buttons
        Button btnTakePicture = view.findViewById(R.id.btnTakePicture);
        Button btnChangeMode = view.findViewById(R.id.btnDisplayQR);

        // ProgressDialog
        progressDialog = new ProgressDialog(getActivity());

        // OnClick Button Take Picture
        btnTakePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: btnTakePicture");
                progressDialog.setMessage("Analysing picture...");
                progressDialog.show();

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

        // OnClick Button open QR code
        btnChangeMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: btnChangeMode");

                // Launch profile fragment
                Fragment displayQR= new DisplayQR();
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.fragmentContainer, displayQR);
                transaction.addToBackStack(null);  // if written, this transaction will be added to backstack
                transaction.commit();
            }
        });

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        cameraKitView = view.findViewById(R.id.camera);
    }

    // FIXME: Camera stops if you tab out of the app and then back in. Minor bug though...
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

    // Get QR Results
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

                        if (barcodes.size() > 0) {
                            for (FirebaseVisionBarcode barcode : barcodes) {
                                //Rect bounds = barcode.getBoundingBox();
                                //Point[] corners = barcode.getCornerPoints();

                                String rawValue = barcode.getRawValue();
                                int valueType = barcode.getValueType();

                                // See API reference for complete list of supported types
                                Log.d(TAG, "onSuccess: " + valueType);
                                Log.d(TAG, "onSuccess: " + rawValue);

                                // Sets value
                                qrCodeInfo = rawValue;

                                progressDialog.cancel();

                                // Launches new activity
                                startActivity(new Intent(getActivity(), UserFound.class));
                            }
                        } else {
                            Toast.makeText(getActivity(), "Unable to identify QR Code! Try zooming in/out a bit", Toast.LENGTH_SHORT).show();
                            progressDialog.cancel();
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

