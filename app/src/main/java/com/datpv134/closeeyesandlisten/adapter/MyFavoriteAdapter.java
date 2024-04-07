package com.datpv134.closeeyesandlisten.adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.datpv134.closeeyesandlisten.R;
import com.datpv134.closeeyesandlisten.model.MyFavoriteSong;

import java.util.List;

public class MyFavoriteAdapter extends RecyclerView.Adapter<MyFavoriteAdapter.ViewHolder> {
    List<MyFavoriteSong> favoriteSongs;
    Context context;
    IOnClickFavoriteSong iOnClickFavoriteSong;

    public MyFavoriteAdapter(List<MyFavoriteSong> favoriteSongs, Context context, IOnClickFavoriteSong iOnClickFavoriteSong) {
        this.favoriteSongs = favoriteSongs;
        this.context = context;
        this.iOnClickFavoriteSong = iOnClickFavoriteSong;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view =  inflater.inflate(R.layout.item_fav, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MyFavoriteSong myFavoriteSong = favoriteSongs.get(position);

        if (myFavoriteSong == null) return;

        Glide.with(this.context)
                .load(myFavoriteSong.getImage())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.imgFav);

        holder.tvSongName.setText(myFavoriteSong.getName());
        holder.tvSongAuthor.setText(myFavoriteSong.getAuthor());

        holder.imgFav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                iOnClickFavoriteSong.onClickFavSong(myFavoriteSong);
            }
        });

        holder.iconFav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                iOnClickFavoriteSong.onClickFavIcon(myFavoriteSong);
            }
        });

        holder.vFavName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                iOnClickFavoriteSong.onClickFavSong(myFavoriteSong);
            }
        });
    }

    @Override
    public int getItemCount() {
        return favoriteSongs.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgFav, iconFav;
        TextView tvSongName, tvSongAuthor;
        RelativeLayout vFavName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imgFav = itemView.findViewById(R.id.imgSongFav);
            iconFav = itemView.findViewById(R.id.iconFavInFragment);
            tvSongName = itemView.findViewById(R.id.tvFavSongName);
            tvSongAuthor = itemView.findViewById(R.id.tvFavSongAuthor);
            vFavName = itemView.findViewById(R.id.vFavName);
        }
    }
}
