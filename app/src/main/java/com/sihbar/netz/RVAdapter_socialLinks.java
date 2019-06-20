package com.sihbar.netz;

import android.content.Context;
import android.support.annotation.NonNull;
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

import java.util.ArrayList;

public class RVAdapter_socialLinks extends RecyclerView.Adapter<RVAdapter_socialLinks.ViewHolder> {

    private static final String TAG = "RVAdapter_socialLinks";

    private Context context;
    ArrayList<Integer> arrayLogos;
    private ArrayList<String> arrayLinks;

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

        // TODO: onClickListener para o editar e apagar. Minuto 15:45 de https://www.youtube.com/watch?v=Vyqz_-sJGFk
        viewHolder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: Clicked!");
                deleteLink(arrayLinks.get(i));
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

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            logo = itemView.findViewById(R.id.imageViewLogo);
            link = itemView.findViewById(R.id.textViewLink);
            parentLayout = itemView.findViewById(R.id.ParentLayout);
            btnDelete = itemView.findViewById(R.id.btnDelete);
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
                        // TODO: Atualizar a pagina/recyclerView quando se apaga um link. Mudar de actiovity resolve o prblema, mas e chato, Devia atualziar sozinho.
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "onFailure: Failed removing link: " + e);
                    }
                });
    }
}
