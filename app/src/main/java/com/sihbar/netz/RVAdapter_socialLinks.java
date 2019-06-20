package com.sihbar.netz;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class RVAdapter_socialLinks extends RecyclerView.Adapter<RVAdapter_socialLinks.ViewHolder> {

    private static final String TAG = "RVAdapter_socialLinks";

    private Context context;
    ArrayList<Integer> arrayLogos;
    private ArrayList<String> arrayLinks;

    // Firebase
    FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

    public RVAdapter_socialLinks(Context context, ArrayList<Integer> arrayLogos, ArrayList<String> arrayLinks) {
        this.context = context;
        this.arrayLogos = arrayLogos;
        this.arrayLinks = arrayLinks;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_social_links, viewGroup, false);
        ViewHolder holder = new ViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, final int i) {
        //Log.d(TAG, "onBindViewHolder: " + arrayLinks.get(i));

        // Set image logos
        Glide.with(context)
                .load(arrayLogos.get(i))
                .into(viewHolder.logo);

        // Set link text
        viewHolder.link.setText(arrayLinks.get(i));

        // Deletes link
        viewHolder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: Clicked delete link!");
                deleteLink(arrayLinks.get(i));
            }
        });

        // Opens dialog to edit link
        viewHolder.btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: Clicked edit link!");
                showDialog(arrayLinks.get(i));
            }
        });

        // opens link on browser
        viewHolder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: Clicked link!");
                openLink(arrayLinks.get(i));
            }
        });
    }

    @Override
    public int getItemCount() {
        return arrayLinks.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        // Variables
        ImageView logo;
        TextView link;
        LinearLayout parentLayout;
        ImageButton btnDelete;
        ImageButton btnEdit;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            logo = itemView.findViewById(R.id.imageViewLogo);
            link = itemView.findViewById(R.id.textViewLink);
            parentLayout = itemView.findViewById(R.id.ParentLayout);
            btnDelete = itemView.findViewById(R.id.btnDelete);
            btnEdit = itemView.findViewById(R.id.btnEdit);
        }
    }

    // Deletes link
    public void deleteLink(final String link) {
        Log.d(TAG, "deleteLink: ");

        FirebaseFirestore.getInstance().collection("users").document(Home.userInfo.getId()).update("links", FieldValue.arrayRemove(link))
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "onSuccess: Removed link successfully: " + link);
                        Toast.makeText(context, "Link removed!", Toast.LENGTH_SHORT).show();

                        // Refresh page
                        refreshPage();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "onFailure: Failed removing link: " + e);
                    }
                });


    }

    // Shows Dialog
    public void showDialog(final String link) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Write the full link please:");

        // Set up the input
        final EditText input = new EditText(context);
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("Edit Link", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // First remove link, and then add it
                // Updates Document
                DocumentReference userRef = firebaseFirestore.collection("users").document(Home.userInfo.getId());
                userRef.update("links", FieldValue.arrayRemove(link))
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d(TAG, "onSuccess: Deleted link!");

                                // Updates Document
                                DocumentReference userRef = firebaseFirestore.collection("users").document(Home.userInfo.getId());
                                userRef.update("links", FieldValue.arrayUnion(input.getText().toString()))
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Log.d(TAG, "onSuccess: Added link!");

                                                // Refresh page
                                                refreshPage();
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.d(TAG, "onFailure: " + e);
                                            }
                                        });
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d(TAG, "onFailure: " + e);
                            }
                        });
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    // Opens social link
    public void openLink(final String link) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://" + link));
        context.startActivity(browserIntent);
    }

    // Refreshes page
    public void refreshPage() {
        AppCompatActivity activity = (AppCompatActivity) context;
        Fragment myFragment = new ProfileFragment();
        activity.getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, myFragment).addToBackStack(null).commit();
    }
}
