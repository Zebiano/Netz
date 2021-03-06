package com.sihbar.netz;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class IntroAddInfo extends AppCompatActivity {

    // Variables
    private static final String TAG = "IntroAddInfo";
    static DocumentSnapshot userInfo;
    DocumentReference userRef;

    private int PICK_IMAGE_REQUEST = 1;
    public Bitmap picture;
    ImageView imageView;
    EditText editTextBio;

    ProgressDialog progressDialog;

    // Firebase
    FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro_add_info);

        progressDialog = new ProgressDialog(this);

        // gets User data
        getUserData();
    }

    // Buttonclick for adding a picture
    public void addPicture(View view) {
        // Opens a menu for the user to choose a picture
        Intent galleryintent = new Intent(Intent.ACTION_GET_CONTENT, null);
        galleryintent.setType("image/*");

        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);

        Intent chooser = new Intent(Intent.ACTION_CHOOSER);
        chooser.putExtra(Intent.EXTRA_INTENT, galleryintent);
        chooser.putExtra(Intent.EXTRA_TITLE, "Pick an option:");

        Intent[] intentArray = {cameraIntent};
        chooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentArray);
        startActivityForResult(Intent.createChooser(chooser, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Sets the picture variable
        imageView = findViewById(R.id.imageView7);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            //Log.d(TAG, "onActivityResult: Picture taken! " + data.getData());

            Uri uri = data.getData();
            try {
                picture = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                Log.d(TAG, String.valueOf(picture));
                imageView.setImageBitmap(picture);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getExtras().get("data") != null) {
            //Log.d(TAG, "onActivityResult: Picture chosen! " + data.getExtras().get("data"));

            picture = (Bitmap) data.getExtras().get("data");
            imageView.setImageBitmap(picture);
        }
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

    // Saves picture and bio
    public void saveInfo(final View view) {
        // Set EditTextBio
        editTextBio = findViewById(R.id.editTextBio);
        final String bio = editTextBio.getText().toString();
        Log.d(TAG, "saveInfo: " + editTextBio.getText());

        progressDialog.setMessage("Saving info...");
        progressDialog.show();

        // Saves what is necessary to save
        if (picture != null && !editTextBio.getText().toString().trim().isEmpty()) {
            Log.d(TAG, "saveInfo: Picture + Bio");

            // Variables
            StorageReference profilePicRef = firebaseStorage.getReference().child("profilepic/" + userInfo.get("userId"));
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            picture.compress(Bitmap.CompressFormat.PNG, 100, baos);
            byte[] data = baos.toByteArray();

            // Save Picture to Storage
            UploadTask uploadTask = profilePicRef.putBytes(data);
            uploadTask
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                            Log.d(TAG, "onSuccess: Save Picture to storage!");

                            // Save bio to FireStore
                            DocumentReference userRef = firebaseFirestore.collection("users").document(userInfo.getId());
                            userRef.update("bio", bio)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Log.d(TAG, "onSuccess: Added bio!");

                                            // Redirects to home
                                            launchHome(view);
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.d(TAG, "onFailure: Failed adding new link..." + e);
                                            progressDialog.cancel();
                                        }
                                    });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            // Handle unsuccessful uploads
                            Log.w(TAG, "onFailure: Rip saving picture to storage", exception);
                            progressDialog.cancel();
                        }
                    });
        } else if (picture != null && editTextBio.getText().toString().trim().isEmpty()) {
            Log.d(TAG, "saveInfo: Picture only");

            // Variables
            StorageReference profilePicRef = firebaseStorage.getReference().child("profilepic/" + userInfo.get("userId"));
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            picture.compress(Bitmap.CompressFormat.PNG, 100, baos);
            byte[] data = baos.toByteArray();

            // Save to Storage
            UploadTask uploadTask = profilePicRef.putBytes(data);
            uploadTask
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                            Log.d(TAG, "onSuccess: Save Picture to storage!");

                            // Redirects to home
                            launchHome(view);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            // Handle unsuccessful uploads
                            Log.w(TAG, "onFailure: Rip saving picture to storage", exception);
                            progressDialog.cancel();
                        }
                    });
        } else if (!editTextBio.getText().toString().trim().isEmpty()) {
            Log.d(TAG, "saveInfo: Bio only");

            // Save bio to FireStore
            DocumentReference userRef = firebaseFirestore.collection("users").document(userInfo.getId());
            userRef.update("bio", bio)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d(TAG, "onSuccess: Added bio!");

                            // Saves Placeholder Profile Picture for the user
                            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.placeholder);
                            StorageReference profilePicRef = firebaseStorage.getReference().child("profilepic/" + userInfo.get("userId"));

                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
                            byte[] data = baos.toByteArray();

                            UploadTask uploadTask = profilePicRef.putBytes(data);
                            uploadTask
                                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                            // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                                            Log.d(TAG, "onSuccess: Save Pic to storage!");
                                            //Toast.makeText(IntroAddInfo.this, "Successfully saved plaveholder picture to storage!", Toast.LENGTH_SHORT).show();

                                            // Redirects to home
                                            launchHome(view);
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception exception) {
                                            // Handle unsuccessful uploads
                                            Log.w(TAG, "onFailure: Rip saving placeholder picture to storage", exception);
                                            progressDialog.cancel();
                                        }
                                    });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d(TAG, "onFailure: Failed adding new link..." + e);
                            progressDialog.cancel();
                        }
                    });
        } else {
            Log.d(TAG, "saveInfo: Nothing");

            // Saves Placeholder Profile Picture for the user
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.placeholder);
            StorageReference profilePicRef = firebaseStorage.getReference().child("profilepic/" + userInfo.get("userId"));

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
            byte[] data = baos.toByteArray();

            UploadTask uploadTask = profilePicRef.putBytes(data);
            uploadTask
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                            Log.d(TAG, "onSuccess: Save Pic to storage!");
                            //Toast.makeText(IntroAddInfo.this, "Successfully saved plaveholder picture to storage!", Toast.LENGTH_SHORT).show();

                            // Redirects to home
                            launchHome(view);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            // Handle unsuccessful uploads
                            Log.w(TAG, "onFailure: Rip saving placeholder picture to storage", exception);
                            progressDialog.cancel();
                        }
                    });
        }
    }

    // Launches Home
    public void launchHome(View view) {
        progressDialog.cancel();

        // Redirect to login
        startActivity(new Intent(this, Home.class));
    }
}
