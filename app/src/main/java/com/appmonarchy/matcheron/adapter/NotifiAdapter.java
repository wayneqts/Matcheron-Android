package com.appmonarchy.matcheron.adapter;

import android.content.Intent;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.appmonarchy.matcheron.R;
import com.appmonarchy.matcheron.activities.BaseActivity;
import com.appmonarchy.matcheron.activities.OtherUser;
import com.appmonarchy.matcheron.databinding.ItemNotificationBinding;
import com.appmonarchy.matcheron.model.Notifications;
import com.google.gson.JsonObject;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotifiAdapter extends RecyclerView.Adapter<NotifiAdapter.VH> {
    List<Notifications> list;
    BaseActivity activity;

    public NotifiAdapter(List<Notifications> list, BaseActivity activity) {
        this.list = list;
        this.activity = activity;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemNotificationBinding binding = ItemNotificationBinding.inflate(LayoutInflater.from(parent.getContext()));
        return new VH(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        Notifications noti = list.get(position);
        holder.bind.tvNotification.setText(noti.getDes());
        holder.bind.tvTime.setText(activity.tool.fmTimeAgo(noti.getTime()));
        if (noti.getStt().equals("1")){
            holder.bind.cvUnread.setVisibility(View.GONE);
            Typeface regularFont = ResourcesCompat.getFont(activity, R.font.baloo2_regular);
            holder.bind.tvNotification.setTypeface(regularFont);
        }else {
            holder.bind.cvUnread.setVisibility(View.VISIBLE);
            Typeface boldFont = ResourcesCompat.getFont(activity, R.font.baloo2_bold);
            holder.bind.tvNotification.setTypeface(boldFont);
        }
        holder.itemView.setOnClickListener(v -> {
            if (!noti.getStt().equals("1")){
                seen(noti.getId(), position);
            }
            v.getContext().startActivity(new Intent(activity, OtherUser.class).putExtra("id", noti.getsId()));
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class VH extends RecyclerView.ViewHolder{
        ItemNotificationBinding bind;
        public VH(@NonNull ItemNotificationBinding binding) {
            super(binding.getRoot());
            this.bind = binding;
        }
    }

    // seen notification
    private void seen(String id, int pos){
        activity.api.getDataById("seen.php", id).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()){
                    list.get(pos).setStt("1");
                    notifyItemChanged(pos);
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {

            }
        });
    }
}
