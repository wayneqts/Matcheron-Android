package com.appmonarchy.matcheron.adapter;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.text.Annotation;
import android.text.SpannableString;
import android.text.SpannedString;
import android.text.method.LinkMovementMethod;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.appmonarchy.matcheron.R;
import com.appmonarchy.matcheron.activities.OtherUser;
import com.appmonarchy.matcheron.activities.authentication.Login;
import com.appmonarchy.matcheron.activities.authentication.SignUp;
import com.appmonarchy.matcheron.databinding.ItemPeopleGridBinding;
import com.appmonarchy.matcheron.databinding.PopupRqLoginBinding;
import com.appmonarchy.matcheron.helper.AppConstrains;
import com.appmonarchy.matcheron.helper.CustomTextMarkDown;
import com.appmonarchy.matcheron.helper.Tool;
import com.appmonarchy.matcheron.model.People;
import com.bumptech.glide.Glide;

import java.util.List;

public class GridAdapter extends RecyclerView.Adapter<GridAdapter.VH> {
    Context context;
    List<People> list;
    boolean isLoggedIn;
    Tool tool;

    public GridAdapter(Context context, List<People> list, boolean isLoggedIn) {
        this.context = context;
        this.list = list;
        this.isLoggedIn = isLoggedIn;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemPeopleGridBinding binding = ItemPeopleGridBinding.inflate(LayoutInflater.from(parent.getContext()));
        return new VH(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        tool = new Tool(context);
        People pp = list.get(position);
        Glide.with(context).load(pp.getImg1()).error(R.drawable.no_avatar).centerCrop().into(holder.bind.ivAvatar);
        new Handler().postDelayed(() -> holder.bind.cvImg.setLayoutParams(new LinearLayoutCompat.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, holder.bind.cvImg.getMeasuredWidth())), 100);
        if (isLoggedIn){
            holder.bind.tvName.setText(pp.getfName()+" "+pp.getlName());
            holder.bind.tvReligion.setText(pp.getState()+", U.S.");
            holder.itemView.setOnClickListener(v -> ((Activity) context).startActivityForResult(new Intent(context, OtherUser.class)
                    .putExtra("id", pp.getId()).putExtra("from", "like"), AppConstrains.REFRESH_MAYBE_CODE));
        }else {
            holder.bind.tvReligion.setVisibility(View.GONE);
            holder.bind.tvName.setText(pp.getfName()+" - "+pp.getAge());
            holder.itemView.setOnClickListener(v -> popupRq());
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class VH extends RecyclerView.ViewHolder{
        ItemPeopleGridBinding bind;
        public VH(@NonNull ItemPeopleGridBinding itemPeopleGridBinding) {
            super(itemPeopleGridBinding.getRoot());
            this.bind = itemPeopleGridBinding;
        }
    }

    // popup require login
    private void popupRq(){
        Dialog dialog = new Dialog(context);
        PopupRqLoginBinding rqLoginBinding = PopupRqLoginBinding.inflate(((Activity) context).getLayoutInflater());
        dialog.setContentView(rqLoginBinding.getRoot());
        tool.setupDialog(dialog, Gravity.CENTER, ViewGroup.LayoutParams.WRAP_CONTENT);

        rqLoginBinding.btClose.setOnClickListener(v -> dialog.dismiss());
        SpannedString loginText = (SpannedString) context.getText(R.string.please_login_if_already_member);
        Annotation[] annotationsLogin = loginText.getSpans(0, loginText.length(), Annotation.class);
        SpannableString loginCopy = new SpannableString(loginText);
        for (Annotation annotation : annotationsLogin) {
            if (annotation.getKey().equals("action")) {
                loginCopy.setSpan(
                        createClickSpan(annotation.getValue(), dialog),
                        loginText.getSpanStart(annotation),
                        loginText.getSpanEnd(annotation),
                        SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE
                );
            }
        }
        rqLoginBinding.tvToLogin.setText(loginCopy);
        rqLoginBinding.tvToLogin.setMovementMethod(LinkMovementMethod.getInstance());
        SpannedString signupText = (SpannedString) context.getText(R.string.join_matcheron_free_signup);
        Annotation[] annotationsSignup = signupText.getSpans(0, signupText.length(), Annotation.class);
        SpannableString signupCopy = new SpannableString(signupText);
        for (Annotation annotation : annotationsSignup) {
            if (annotation.getKey().equals("action")) {
                signupCopy.setSpan(
                        createClickSpan(annotation.getValue(), dialog),
                        signupText.getSpanStart(annotation),
                        signupText.getSpanEnd(annotation),
                        SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE
                );
            }
        }
        rqLoginBinding.tvToSignup.setText(signupCopy);
        rqLoginBinding.tvToSignup.setMovementMethod(LinkMovementMethod.getInstance());

        dialog.show();
    }

    // create link span
    private CustomTextMarkDown createClickSpan(String action, Dialog dialog) {
        switch (action.toLowerCase()) {
            case "login":
                return new CustomTextMarkDown(() -> {
                    context.startActivity(new Intent(context, Login.class));
                    dialog.dismiss();
                }, "#3F4E80", context);
            case "signup":
                return new CustomTextMarkDown(() -> {
                    context.startActivity(new Intent(context, SignUp.class));
                    dialog.dismiss();
                }, "#3F4E80", context);
            default:
                throw new UnsupportedOperationException("action " + action + " not implemented");
        }
    }
}
