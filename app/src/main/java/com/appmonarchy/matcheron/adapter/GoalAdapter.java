package com.appmonarchy.matcheron.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.appmonarchy.matcheron.activities.BaseActivity;
import com.appmonarchy.matcheron.activities.authentication.SignUp;
import com.appmonarchy.matcheron.databinding.ItemGoalBinding;
import com.appmonarchy.matcheron.model.Goal;

import java.util.List;

public class GoalAdapter extends RecyclerView.Adapter<GoalAdapter.VH> {
    List<Goal> list;
    public int selectedPosition = 0;
    BaseActivity activity;
    boolean isEdit;

    public GoalAdapter(List<Goal> list, BaseActivity activity, boolean isEdit) {
        this.list = list;
        this.activity = activity;
        this.isEdit = isEdit;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemGoalBinding binding = ItemGoalBinding.inflate(LayoutInflater.from(parent.getContext()));
        return new VH(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        Goal goal = list.get(position);
        holder.bind.rb.setText(goal.getName());
        if (selectedPosition == position){
            activity.goalId = goal.getId();
            holder.bind.rb.setChecked(true);
        }else {
            holder.bind.rb.setChecked(false);
        }
        if (!isEdit){
            holder.bind.rb.setOnClickListener(v -> {
                if (selectedPosition >= 0)
                    notifyItemChanged(selectedPosition);
                selectedPosition = holder.getLayoutPosition();
                notifyItemChanged(selectedPosition);
            });
        }else {
            holder.bind.rb.setEnabled(false);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class VH extends RecyclerView.ViewHolder{
        ItemGoalBinding bind;
        public VH(@NonNull ItemGoalBinding itemGoalBinding) {
            super(itemGoalBinding.getRoot());
            this.bind = itemGoalBinding;
        }
    }
}
