package com.appmonarchy.matcheron.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.appmonarchy.matcheron.R;
import com.appmonarchy.matcheron.databinding.ActivityPrivacyPolicyBinding;

public class PrivacyPolicy extends AppCompatActivity {
    ActivityPrivacyPolicyBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPrivacyPolicyBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btBack.setOnClickListener(v -> finish());
    }
}