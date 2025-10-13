package com.appmonarchy.matcheron.adapter;

import android.app.Dialog;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.media3.common.MediaItem;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.recyclerview.widget.RecyclerView;

import com.appmonarchy.matcheron.activities.BaseActivity;
import com.appmonarchy.matcheron.databinding.ItemChatBinding;
import com.appmonarchy.matcheron.databinding.PopupViewImgBinding;
import com.appmonarchy.matcheron.model.Chat;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.VH> {
    List<Chat> list;
    BaseActivity activity;
    String[] arrType = new String[]{".MOV", ".MP4", ".AVI", ".WebM", ".WMV", ".MKV", ".flv", ".3GP"};

    public ChatAdapter(List<Chat> list, BaseActivity activity) {
        this.list = list;
        this.activity = activity;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemChatBinding binding = ItemChatBinding.inflate(LayoutInflater.from(parent.getContext()));
        return new VH(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        Chat chat = list.get(position);
        if (chat.getSenderId().equals(activity.pref.getUserId())){
            if (!chat.getMedia().equals("")){
                holder.bind.llUser.setVisibility(View.GONE);
                holder.bind.cvUser.setVisibility(View.VISIBLE);
                Glide.with(activity).load(chat.getMedia()).centerCrop().listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        holder.bind.pbLoadingUser.setVisibility(View.GONE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        holder.bind.pbLoadingUser.setVisibility(View.GONE);
                        return false;
                    }
                }).into(holder.bind.ivUserImg);
                holder.bind.ivUserImg.setOnClickListener(v -> popupViewImg(chat.getMedia()));
                for (String s : arrType){
                    if (chat.getMedia().toLowerCase().contains(s.toLowerCase())){
                        holder.bind.btPlayUser.setVisibility(View.VISIBLE);
                        break;
                    }else {
                        holder.bind.btPlayUser.setVisibility(View.GONE);
                    }
                }
            }else {
                holder.bind.cvUser.setVisibility(View.GONE);
                holder.bind.llUser.setVisibility(View.VISIBLE);
            }
            holder.bind.llGuest.setVisibility(View.GONE);
            holder.bind.cvGuest.setVisibility(View.GONE);
            holder.bind.tvUser.setText(chat.getMess());
            holder.bind.ivSeen.setVisibility(chat.getReadStt().equals("0") ? View.VISIBLE : View.GONE);
            holder.bind.tvUserTime.setText(activity.tool.fmTimeAgo(chat.getCreatedAt()));

        }else {
            if (!chat.getMedia().equals("")){
                holder.bind.llGuest.setVisibility(View.GONE);
                holder.bind.cvGuest.setVisibility(View.VISIBLE);
                Glide.with(activity).load(chat.getMedia()).centerCrop().listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        holder.bind.pbLoadingGuest.setVisibility(View.GONE);
                        return false;
                    }
                }).into(holder.bind.ivGuestImg);
                holder.bind.ivGuestImg.setOnClickListener(v -> popupViewImg(chat.getMedia()));
                for (String s : arrType){
                    if (chat.getMedia().toLowerCase().contains(s.toLowerCase())){
                        holder.bind.btPlayGuest.setVisibility(View.VISIBLE);
                        break;
                    }else {
                        holder.bind.btPlayGuest.setVisibility(View.GONE);
                    }
                }
            }else {
                holder.bind.cvGuest.setVisibility(View.GONE);
                holder.bind.llGuest.setVisibility(View.VISIBLE);
            }
            holder.bind.llUser.setVisibility(View.GONE);
            holder.bind.cvUser.setVisibility(View.GONE);
            holder.bind.tvGuest.setText(chat.getMess());
            holder.bind.tvGuestTime.setText(activity.tool.fmTimeAgo(chat.getCreatedAt()));
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class VH extends RecyclerView.ViewHolder{
        ItemChatBinding bind;
        public VH(@NonNull ItemChatBinding binding) {
            super(binding.getRoot());
            this.bind = binding;
        }
    }

    // popup view image
    private void popupViewImg(String imgUrl){
        Dialog dialog = new Dialog(activity);
        PopupViewImgBinding viewImgBinding = PopupViewImgBinding.inflate(activity.getLayoutInflater());
        dialog.setContentView(viewImgBinding.getRoot());
        activity.tool.setupDialog(dialog, Gravity.CENTER, ViewGroup.LayoutParams.MATCH_PARENT);

        for (String s : arrType){
            if (imgUrl.toLowerCase().contains(s.toLowerCase())){
                viewImgBinding.videoView.setVisibility(View.VISIBLE);
                viewImgBinding.zoomView.setVisibility(View.GONE);
                ExoPlayer player = new ExoPlayer.Builder(activity).build();
                viewImgBinding.videoView.setPlayer(player);
                MediaItem mediaItem = MediaItem.fromUri(imgUrl);
                player.setMediaItem(mediaItem);
                player.prepare();
                player.play();
                break;
            }
        }
        viewImgBinding.btClose.setOnClickListener(v -> dialog.dismiss());
        Glide.with(activity).load(imgUrl).centerInside().into(viewImgBinding.zoomView);

        dialog.show();
    }
}
