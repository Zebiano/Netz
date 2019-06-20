package com.sihbar.netz;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class RVAdapter_contacts extends RecyclerView.Adapter<RVAdapter_contacts.ViewHolder> {

    // Variables
    private static final String TAG = "RVAdapter_contacts";

    private Context context;
    ArrayList<StorageReference> arrayImages;
    private ArrayList<String> arrayNames;
    ArrayList<String> arrayUserId;

    public RVAdapter_contacts(Context context, ArrayList<StorageReference> arrayImages, ArrayList<String> arrayNames, ArrayList<String> arrayUserId) {
        this.context = context;
        this.arrayImages = arrayImages;
        this.arrayNames = arrayNames;
        this.arrayUserId = arrayUserId;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_contacts, viewGroup, false);
        RVAdapter_contacts.ViewHolder holder = new ViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, final int i) {
        //Log.d(TAG, "onBindViewHolder: " + arrayLinks.get(i));
        //Log.d(TAG, "onBindViewHolder: " + arrayLogos.get(i));

        // Set image logos
        Glide.with(context)
                .load(arrayImages.get(i))
                .into(viewHolder.image);

        // Set link text
        viewHolder.name.setText(arrayNames.get(i));

        // Opens Contact Profile
        viewHolder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: Clicked Contact!");
                openContact(arrayUserId.get(i));
            }
        });

        // Removes Contact
        viewHolder.btnRemoveContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: Clicked Remove Contact!");
                removeContact(arrayUserId.get(i));
            }
        });

        // Opens Contact Profile
        viewHolder.btnArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: Clicked Contact!");
                openContact(arrayUserId.get(i));
            }
        });
    }

    @Override
    public int getItemCount() {
        return arrayNames.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        // Variables
        ImageView image;
        TextView name;
        LinearLayout parentLayout;
        ImageButton btnRemoveContact;
        ImageButton btnArrow;

        public ViewHolder (@NonNull View itemView) {
            super(itemView);

            image = itemView.findViewById(R.id.imageViewImage);
            name = itemView.findViewById(R.id.textViewName);
            parentLayout = itemView.findViewById(R.id.ParentLayout);
            btnRemoveContact = itemView.findViewById(R.id.btnRemoveContact);
            btnArrow = itemView.findViewById(R.id.btnArrow);
        }
    }

    // Opens the contacts profile
    public void openContact(final String userId) {
        Log.d(TAG, "openContact: " + userId);

        // Redirect to User Profile
        context.startActivity(new Intent(context, UserProfile.class).putExtra("userId", userId));
    }

    // Removes contact
    public void removeContact(final String userId) {
        Log.d(TAG, "removeContact: ");

        FirebaseFirestore.getInstance().collection("users").document(Home.userInfo.getId()).update("contacts", FieldValue.arrayRemove(userId))
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "onSuccess: Removed link successfully: " + userId);
                        Toast.makeText(context, "User removed from contacts!", Toast.LENGTH_SHORT).show();

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

    // Refreshes page
    public void refreshPage() {
        AppCompatActivity activity = (AppCompatActivity) context;
        Fragment myFragment = new ContactsFragment();
        activity.getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, myFragment).addToBackStack(null).commit();
    }
}
