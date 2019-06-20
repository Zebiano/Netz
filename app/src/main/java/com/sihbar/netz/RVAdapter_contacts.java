package com.sihbar.netz;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
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

        public ViewHolder (@NonNull View itemView) {
            super(itemView);

            image = itemView.findViewById(R.id.imageViewImage);
            name = itemView.findViewById(R.id.textViewName);
            parentLayout = itemView.findViewById(R.id.ParentLayout);
        }
    }

    // Opens the contacts profile
    public void openContact(final String userId) {
        Log.d(TAG, "openContact: " + userId);

        // Redirect to User Profile
        context.startActivity(new Intent(context, UserProfile.class).putExtra("userId", userId));
    }
}
