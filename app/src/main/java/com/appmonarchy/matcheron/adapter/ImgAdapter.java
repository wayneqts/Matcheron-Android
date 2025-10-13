package com.appmonarchy.matcheron.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.appmonarchy.matcheron.activities.authentication.SignUp;
import com.appmonarchy.matcheron.databinding.ItemUploadImgBinding;
import com.bumptech.glide.Glide;

import java.util.List;

public class ImgAdapter extends RecyclerView.Adapter<ImgAdapter.VH> {
    List<Bitmap> list;
    SignUp context;

    public ImgAdapter(List<Bitmap> list, SignUp context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemUploadImgBinding binding = ItemUploadImgBinding.inflate(LayoutInflater.from(parent.getContext()));
        return new VH(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        Glide.with(context).load(list.get(position)).centerCrop().into(holder.bind.ivImg);
        holder.bind.btSelect.setOnClickListener(v -> {
            context.selectType = "update";
            context.crPos = holder.getAdapterPosition();
            context.popupSelectPhoto();
        });
        holder.bind.btDelete.setOnClickListener(v -> {
            context.binding.btChooseFile.setVisibility(View.VISIBLE);
            list.remove(position);
            notifyItemRemoved(position);
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class VH extends RecyclerView.ViewHolder{
        ItemUploadImgBinding bind;
        public VH(@NonNull ItemUploadImgBinding itemUploadImgBinding) {
            super(itemUploadImgBinding.getRoot());
            this.bind = itemUploadImgBinding;
        }
    }
}
