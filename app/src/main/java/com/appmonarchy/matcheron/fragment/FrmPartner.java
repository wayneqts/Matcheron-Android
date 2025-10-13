package com.appmonarchy.matcheron.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.appmonarchy.matcheron.R;
import com.appmonarchy.matcheron.activities.Home;
import com.appmonarchy.matcheron.databinding.FrmPartnerBinding;

public class FrmPartner extends Fragment {
    FrmPartnerBinding binding;
    Home home;
    public FrmPartner() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        home = (Home) getActivity();
        binding = FrmPartnerBinding.inflate(getLayoutInflater());

        binding.cvCc.setOnClickListener(v -> {
            openApp("com.circlecueinc.circlecue");
        });
        binding.cvMc.setOnClickListener(v -> openApp("com.appmonarchy.matcheron"));
        binding.cvKk.setOnClickListener(v -> openApp("com.appmonarchy.karkonnex"));
        binding.cvRr.setOnClickListener(v -> openApp("com.app.roomrently"));

        return binding.getRoot();
    }

    // open app in play store
    private void openApp(String pkName){
        Intent intent = new Intent(Intent.ACTION_VIEW,
                Uri.parse("https://play.google.com/store/apps/details?id=" + pkName));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}