package com.appmonarchy.matcheron.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.LinearLayoutCompat;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.appmonarchy.matcheron.R;
import com.appmonarchy.matcheron.databinding.ActivitySplashBinding;

public class Splash extends BaseActivity {
    ActivitySplashBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySplashBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.ivLogo.setLayoutParams(new LinearLayoutCompat.LayoutParams((int) (tool.getWidthScreen() / 1.48), (int) (tool.getWidthScreen() / 2.07)));
        new Handler().postDelayed(() -> {
            if (pref.getUserId().equals("")) {
                startActivity(new Intent(this, GetStarted.class));
            }else {
                startActivity(new Intent(this, Home.class));
            }
            finish();
        }, 2000);
    }
}