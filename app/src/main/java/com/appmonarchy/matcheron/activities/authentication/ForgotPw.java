package com.appmonarchy.matcheron.activities.authentication;

import android.os.Bundle;

import com.appmonarchy.matcheron.activities.BaseActivity;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;

import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import androidx.core.view.WindowCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.appmonarchy.matcheron.databinding.ActivityForgotPwBinding;

import com.appmonarchy.matcheron.R;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForgotPw extends BaseActivity {
    ActivityForgotPwBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityForgotPwBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btBack.setOnClickListener(v -> finish());
        binding.btSend.setOnClickListener(v -> {
            String email = binding.etEmail.getText().toString().trim();
            if (TextUtils.isEmpty(email)){
                Toast.makeText(this, getString(R.string.email_is_required), Toast.LENGTH_SHORT).show();
            }else if (!tool.isEmailValid(email)){
                Toast.makeText(this, getString(R.string.email_is_invalid), Toast.LENGTH_SHORT).show();
            }else{
                tool.showLoading();
                forgotPw();
            }
        });
    }

    // forgot password
    private void forgotPw(){
        RequestBody rqEmail = RequestBody.create(MediaType.parse("text/plain"), binding.etEmail.getText().toString().trim());
        api.forgotPw(rqEmail).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()){
                    try {
                        JSONObject jsObj = new JSONObject(String.valueOf(response.body()));
                        if (jsObj.optBoolean("Status")){
                            Toast.makeText(ForgotPw.this, jsObj.optString("Message"), Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                tool.hideLoading();
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                tool.hideLoading();
            }
        });
    }
}