package com.sihbar.netz;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
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
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class RVAdapter_socialLinks_userFound extends RecyclerView.Adapter<RVAdapter_socialLinks_userFound.ViewHolder> {

    private static final String TAG = "RVAdapter_socialLinks";

    private Context context;
    ArrayList<Integer> arrayLogos;
    private ArrayList<String> arrayLinks;

    // Firebase
    FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

    public RVAdapter_socialLinks_userFound(Context context, ArrayList<Integer> arrayLogos, ArrayList<String> arrayLinks) {
        this.context = context;
        this.arrayLogos = arrayLogos;
        this.arrayLinks = arrayLinks;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_social_links_founduser, viewGroup, false);
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

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            logo = itemView.findViewById(R.id.imageViewLogo);
            link = itemView.findViewById(R.id.textViewLink);
            parentLayout = itemView.findViewById(R.id.ParentLayout);
        }
    }

    // Opens social link
    public void openLink(final String link) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://" + link));
        context.startActivity(browserIntent);
    }
}
