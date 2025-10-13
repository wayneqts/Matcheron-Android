package com.appmonarchy.matcheron.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.appmonarchy.matcheron.activities.OtherUser;
import com.appmonarchy.matcheron.databinding.ItemHomePeopleBinding;
import com.appmonarchy.matcheron.helper.Tool;
import com.appmonarchy.matcheron.model.People;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class HomePeopleAdapter extends RecyclerView.Adapter<HomePeopleAdapter.VH> {
    List<People> list;
    Context context;
    Tool tool;

    public HomePeopleAdapter(List<People> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemHomePeopleBinding binding = ItemHomePeopleBinding.inflate(LayoutInflater.from(parent.getContext()));
        return new VH(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        tool = new Tool(context);
        People people = list.get(position);
        holder.bind.tvName.setText(people.getfName()+" - "+people.getAge());
        Calendar c = Calendar.getInstance();
        c.setTime(tool.stringToDate(people.getCreated()));
        c.add(Calendar.DAY_OF_MONTH, 2);
        if (new Date().after(c.getTime())){
            if (people.getGender().equals("Male")){
                holder.bind.tvName.setText(Html.fromHtml(people.getfName()+" - "+people.getAge()+" \uD83E\uDD85", Html.FROM_HTML_MODE_COMPACT));
            }else if (people.getGender().equals("Female")){
                holder.bind.tvName.setText(Html.fromHtml(people.getfName()+" - "+people.getAge()+" \uD83E\uDD8B", Html.FROM_HTML_MODE_COMPACT));
            }else {
                holder.bind.tvName.setText(people.getfName()+" - "+people.getAge());
            }
        }else {
            holder.bind.tvName.setText(Html.fromHtml(people.getfName()+" - <font color=#000000>"+people.getAge()+"</font>", Html.FROM_HTML_MODE_COMPACT));
        }
        holder.bind.tvProfession.setText(people.getProfession());
        holder.bind.tvBorn.setText(people.getState()+", U.S.");
        holder.bind.pbLoading.setVisibility(View.VISIBLE);
        if (!people.getImg1().equals("")){
            Glide.with(context).load(people.getImg1()).centerCrop().listener(new RequestListener<Drawable>() {
                @Override
                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                    return false;
                }

                @Override
                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                    holder.bind.pbLoading.setVisibility(View.GONE);
                    return false;
                }
            }).into(holder.bind.iv);
        }else {
            holder.bind.pbLoading.setVisibility(View.GONE);
        }
        holder.itemView.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        holder.bind.iv.setOnClickListener(v -> context.startActivity(new Intent(context, OtherUser.class).putExtra("id", people.getId())));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class VH extends RecyclerView.ViewHolder{
        ItemHomePeopleBinding bind;
        public VH(@NonNull ItemHomePeopleBinding itemHomePeopleBinding) {
            super(itemHomePeopleBinding.getRoot());
            this.bind = itemHomePeopleBinding;
        }
    }
}
