package com.appmonarchy.matcheron.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.appmonarchy.matcheron.activities.EventDetail;
import com.appmonarchy.matcheron.databinding.ItemMixersBinding;
import com.appmonarchy.matcheron.model.Event;
import com.bumptech.glide.Glide;

import java.util.List;

public class MixerAdapter extends RecyclerView.Adapter<MixerAdapter.VH> {
    Context context;
    List<Event> list;

    public MixerAdapter(Context context, List<Event> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemMixersBinding binding = ItemMixersBinding.inflate(LayoutInflater.from(parent.getContext()));
        return new VH(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        Event ev = list.get(position);
        holder.bind.tvName.setText(ev.getName());
        Glide.with(context).load(ev.getPhoto()).centerCrop().into(holder.bind.iv);
        holder.itemView.setOnClickListener(v -> context.startActivity(new Intent(context, EventDetail.class).putExtra("id", ev.getId())));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class VH extends RecyclerView.ViewHolder{
        ItemMixersBinding bind;
        public VH(@NonNull ItemMixersBinding binding) {
            super(binding.getRoot());
            this.bind = binding;
        }
    }
}
