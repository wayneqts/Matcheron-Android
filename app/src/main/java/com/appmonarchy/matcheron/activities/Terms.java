package com.appmonarchy.matcheron.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.appmonarchy.matcheron.R;
import com.appmonarchy.matcheron.databinding.ActivityTermsBinding;

public class Terms extends AppCompatActivity {
    ActivityTermsBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTermsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btBack.setOnClickListener(v -> finish());
    }
}