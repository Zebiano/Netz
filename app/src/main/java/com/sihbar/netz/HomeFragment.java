package com.sihbar.netz;

import android.app.Activity;
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

    // Qr code options
    FirebaseVisionBarcodeDetectorOptions barcodeOptions =
            new FirebaseVisionBarcodeDetectorOptions.Builder()
                    .setBarcodeFormats(
                            FirebaseVisionBarcode.FORMAT_QR_CODE)
                    .build();

    // Camera rotation settings
    private static final SparseIntArray ORIENTATIONS = new SparseIntArray();

    static {
        ORIENTATIONS.append(Surface.ROTATION_0, 90);
        ORIENTATIONS.append(Surface.ROTATION_90, 0);
        ORIENTATIONS.append(Surface.ROTATION_180, 270);
        ORIENTATIONS.append(Surface.ROTATION_270, 180);
    }

    /**
     * Get the angle by which an image must be rotated given the device's current
     * orientation.
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private int getRotationCompensation(String cameraId, Activity activity, Context context)
            throws CameraAccessException {
        // Get the device's current rotation relative to its "native" orientation.
        // Then, from the ORIENTATIONS table, look up the angle the image must be
        // rotated to compensate for the device's rotation.
        int deviceRotation = activity.getWindowManager().getDefaultDisplay().getRotation();
        int rotationCompensation = ORIENTATIONS.get(deviceRotation);

        // On most devices, the sensor orientation is 90 degrees, but for some
        // devices it is 270 degrees. For devices with a sensor orientation of
        // 270, rotate the image an additional 180 ((270 + 270) % 360) degrees.
        CameraManager cameraManager = (CameraManager) context.getSystemService(CAMERA_SERVICE);
        int sensorOrientation = cameraManager
                .getCameraCharacteristics(cameraId)
                .get(CameraCharacteristics.SENSOR_ORIENTATION);
        rotationCompensation = (rotationCompensation + sensorOrientation + 270) % 360;

        // Return the corresponding FirebaseVisionImageMetadata rotation value.
        int result;
        switch (rotationCompensation) {
            case 0:
                result = FirebaseVisionImageMetadata.ROTATION_0;
                break;
            case 90:
                result = FirebaseVisionImageMetadata.ROTATION_90;
                break;
            case 180:
                result = FirebaseVisionImageMetadata.ROTATION_180;
                break;
            case 270:
                result = FirebaseVisionImageMetadata.ROTATION_270;
                break;
            default:
                result = FirebaseVisionImageMetadata.ROTATION_0;
                Log.e(TAG, "Bad rotation value: " + rotationCompensation);
        }
        return result;
    }

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
                        Log.d(TAG, "onImage: " + capturedImage);

                        File savedPhoto = new File(Environment.getExternalStorageDirectory(), "photo.jpg");
                        try {
                            Log.d(TAG, "onImage: ");
                            FileOutputStream outputStream = new FileOutputStream(savedPhoto.getPath());
                            outputStream.write(capturedImage);
                            outputStream.close();

                            FirebaseVisionImage image;
                            Uri uri = Uri.fromFile(savedPhoto);
                            try {
                                image = FirebaseVisionImage.fromFilePath(getActivity(), uri);

                                // Set detector with barcodeOptions
                                FirebaseVisionBarcodeDetector detector = FirebaseVision.getInstance()
                                        .getVisionBarcodeDetector();
                                //.getVisionBarcodeDetector(barcodeOptions);

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
                                                    /*switch (valueType) {
                                                        case FirebaseVisionBarcode.TYPE_WIFI:
                                                            String ssid = barcode.getWifi().getSsid();
                                                            String password = barcode.getWifi().getPassword();
                                                            int type = barcode.getWifi().getEncryptionType();
                                                            break;
                                                        case FirebaseVisionBarcode.TYPE_URL:
                                                            String title = barcode.getUrl().getTitle();
                                                            String url = barcode.getUrl().getUrl();
                                                            break;
                                                    }*/
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
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        } catch (java.io.IOException e) {
                            e.printStackTrace();
                            Log.w(TAG, "onImage: ", e);
                        }

                        /*FirebaseVisionImageMetadata metadata = new FirebaseVisionImageMetadata.Builder()
                                .setWidth(480)   // 480x360 is typically sufficient for
                                .setHeight(360)  // image recognition
                                .setFormat(FirebaseVisionImageMetadata.IMAGE_FORMAT_NV21)
                                .setRotation(0)
                                .build();

                        // Load byteArray image to firebaseVision
                        FirebaseVisionImage image = FirebaseVisionImage.fromByteArray(capturedImage, metadata);

                        // Set detector with barcodeOptions
                        FirebaseVisionBarcodeDetector detector = FirebaseVision.getInstance()
                                .getVisionBarcodeDetector(barcodeOptions);

                        Task<List<FirebaseVisionBarcode>> result = detector.detectInImage(image)
                                .addOnSuccessListener(new OnSuccessListener<List<FirebaseVisionBarcode>>() {
                                    @Override
                                    public void onSuccess(List<FirebaseVisionBarcode> barcodes) {
                                        // Task completed successfully
                                        Log.d(TAG, "onSuccess: " + barcodes);

                                        for (FirebaseVisionBarcode barcode: barcodes) {
                                            Rect bounds = barcode.getBoundingBox();
                                            Point[] corners = barcode.getCornerPoints();

                                            String rawValue = barcode.getRawValue();

                                            int valueType = barcode.getValueType();
                                            // See API reference for complete list of supported types
                                            switch (valueType) {
                                                case FirebaseVisionBarcode.TYPE_WIFI:
                                                    String ssid = barcode.getWifi().getSsid();
                                                    String password = barcode.getWifi().getPassword();
                                                    int type = barcode.getWifi().getEncryptionType();
                                                    break;
                                                case FirebaseVisionBarcode.TYPE_URL:
                                                    String title = barcode.getUrl().getTitle();
                                                    String url = barcode.getUrl().getUrl();
                                                    break;
                                            }
                                        }
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        // Task failed with an exception
                                        Log.w(TAG, "onFailure: ", e);
                                    }
                                });*/
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

    @Override
    public void onStart() {
        Log.d(TAG, "onStart: ");
        super.onStart();
        cameraKitView.onStart();
    }

    // If you uncomment this camera will stop working
    /*@Override
    public void onResume() {
        Log.d(TAG, "onResume: ");
        super.onResume();
        cameraKitView.onResume();
    }
    @Override
    public void onPause() {
        Log.d(TAG, "onPause: ");
        cameraKitView.onPause();
        super.onPause();
    }*/
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
}
