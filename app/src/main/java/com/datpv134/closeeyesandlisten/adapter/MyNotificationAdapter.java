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
import com.datpv134.closeeyesandlisten.model.MyNotification;

import java.util.ArrayList;
import java.util.List;

public class MyNotificationAdapter extends RecyclerView.Adapter<MyNotificationAdapter.ViewHolder> {
    List<MyNotification> notifications;
    Context context;
    IOnClickNotification iOnClickNotification;

    public MyNotificationAdapter(ArrayList<MyNotification> notifications, Context context, IOnClickNotification iOnClickNotification) {
        this.notifications = notifications;
        this.context = context;
        this.iOnClickNotification = iOnClickNotification;
    }

    @NonNull
    @Override
    public MyNotificationAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_notifi, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyNotificationAdapter.ViewHolder holder, int position) {
        MyNotification notification = notifications.get(position);

        Glide.with(this.context)
                .load(notification.getImage())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.imgSongNotifi);

        holder.tvNotifiSongName.setText(notification.getName());
        holder.tvNotifiSongAuthor.setText(notification.getAuthor());
        holder.tvUpdateTime.setText(notification.getUpdateTime());

        holder.layoutOnClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                iOnClickNotification.onClickNotification(notification);
            }
        });
    }

    @Override
    public int getItemCount() {
        return notifications.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgSongNotifi;
        TextView tvNotifiSongName, tvNotifiSongAuthor, tvUpdateTime;
        RelativeLayout layoutOnClick;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            layoutOnClick = itemView.findViewById(R.id.layoutOnClickNotifi);
            imgSongNotifi = itemView.findViewById(R.id.imgSongNotifi);
            tvNotifiSongName = itemView.findViewById(R.id.tvNotifiSongName);
            tvNotifiSongAuthor = itemView.findViewById(R.id.tvNotifiSongAuthor);
            tvUpdateTime = itemView.findViewById(R.id.tvUpdateTime);
        }
    }
}
