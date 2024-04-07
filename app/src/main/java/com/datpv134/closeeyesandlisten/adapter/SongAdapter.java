package com.datpv134.closeeyesandlisten.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.datpv134.closeeyesandlisten.R;
import com.datpv134.closeeyesandlisten.model.Song;

import java.util.List;

public class SongAdapter extends RecyclerView.Adapter<SongAdapter.ViewHolder> {
    List<Song> songList;
    Context context;
    IOnClickSong iOnClickSong;

    public SongAdapter(List<Song> songList, Context context, IOnClickSong iOnClickSong) {
        this.songList = songList;
        this.context = context;
        this.iOnClickSong = iOnClickSong;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_song, parent, false);

        ViewHolder holder = new ViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Song song = songList.get(position);

        Glide.with(this.context)
                .load(song.getImage())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.imgSong);

        holder.imgSong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                iOnClickSong.onClickSong(song);
            }
        });

        holder.name.setText(song.getName());
        holder.author.setText(song.getAuthor());
    }

    @Override
    public int getItemCount() {
        return songList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgSong;
        TextView name, author;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imgSong = itemView.findViewById(R.id.imgSong);
            name = itemView.findViewById(R.id.tvName);
            author = itemView.findViewById(R.id.tvAuthor);
        }
    }
}
