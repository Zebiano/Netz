package com.sihbar.netz;

import android.content.Context;
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

import java.util.ArrayList;

public class RVAdapter_socialLinks extends RecyclerView.Adapter<RVAdapter_socialLinks.ViewHolder> {

    private static final String TAG = "RVAdapter_socialLinks";

    private Context context;
    private ArrayList<String> arrayLogos = new ArrayList<>();
    private ArrayList<String> arrayLinks = new ArrayList<>();

    public RVAdapter_socialLinks(Context context, ArrayList<String> arrayLogos, ArrayList<String> arrayLinks) {
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
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        Log.d(TAG, "onBindViewHolder: ");

        Log.d(TAG, "onBindViewHolder: " + arrayLinks);
        Log.d(TAG, "onBindViewHolder: " + arrayLogos);

        // Set image logos
        Glide.with(context)
                .asBitmap()
                .load(arrayLogos.get(i))
                .into(viewHolder.logo);

        // Set link text
        viewHolder.link.setText(arrayLinks.get(i));

        // TODO: onClickListener para o editar e apagar. Minuto 15:45 de https://www.youtube.com/watch?v=Vyqz_-sJGFk
        // TODO: Falta meter os icons dentro do layout_social_links.xml
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
}
