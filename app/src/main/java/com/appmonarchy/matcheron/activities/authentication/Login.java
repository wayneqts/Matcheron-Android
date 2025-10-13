package com.appmonarchy.matcheron.activities.authentication;

import androidx.annotation.Nullable;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.appmonarchy.matcheron.R;
import com.appmonarchy.matcheron.activities.BaseActivity;
import com.appmonarchy.matcheron.activities.EditPf;
import com.appmonarchy.matcheron.activities.Home;
import com.appmonarchy.matcheron.activities.UpdatePfInfo;
import com.appmonarchy.matcheron.databinding.ActivityLoginBinding;
import com.appmonarchy.matcheron.helper.AppConstrains;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.Collections;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Login extends BaseActivity {
    ActivityLoginBinding binding;
    GoogleSignInAccount account;
    int RC_SIGN_IN = 123;
    CallbackManager callbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        init();

        binding.btLogin.setOnClickListener(v -> {
            String email = binding.etEmail.getText().toString().trim();
            String pw = binding.etPw.getText().toString().trim();
            if (TextUtils.isEmpty(email)){
                Toast.makeText(this, getString(R.string.email_is_required), Toast.LENGTH_SHORT).show();
            }else if (!tool.isEmailValid(email)){
                Toast.makeText(this, getString(R.string.email_is_invalid), Toast.LENGTH_SHORT).show();
            }else if (TextUtils.isEmpty(pw)){
                Toast.makeText(this, getString(R.string.password_is_required), Toast.LENGTH_SHORT).show();
            }else {
                tool.showLoading();
                login(email, pw);
            }
        });
        binding.btBack.setOnClickListener(v -> finish());
        binding.tvToSignup.setOnClickListener(v -> startActivity(new Intent(this, SignUp.class)));
        binding.tvFgPw.setOnClickListener(v -> startActivity(new Intent(this, ForgotPw.class)));
        binding.btGg.setOnClickListener(v -> {
            tool.showLoading();
            Intent intentGG = googleSignInClient.getSignInIntent();
            startActivityForResult(intentGG, RC_SIGN_IN);
        });
        binding.btFb.setOnClickListener(v -> LoginManager.getInstance().logInWithReadPermissions(this, Collections.singletonList("public_profile")));
    }

    // init UI
    private void init(){
        tool.tvMarkDown(R.string.by_proceeding_to_use_matcheron_you_agree_to_our_terms_and_privacy_policy, binding.tvTerms);
        callbackManager = CallbackManager.Factory.create();

        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        GraphRequest graphRequest = GraphRequest.newMeRequest(loginResult.getAccessToken(), (jsonObject, graphResponse) -> {
                            if (jsonObject != null){
                                String url = jsonObject.optJSONObject("picture").optJSONObject("data").optString("url");
                                loginSocial(url, jsonObject.optString("name"), jsonObject.optString("email"), "Facebook");
                            }
                        });
                        Bundle parameters = new Bundle();
                        parameters.putString("fields", "id, name, email, picture.type(large)"); // Parámetros que pedimos a facebook
                        graphRequest.setParameters(parameters);
                        graphRequest.executeAsync();
                    }

                    @Override
                    public void onCancel() {
                        Log.e("TAG", "onCancel: " );
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        Toast.makeText(Login.this, exception.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN){
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        String imgUrl;
        try {
            account = completedTask.getResult(ApiException.class);
            if (account.getPhotoUrl()!=null){
                imgUrl = account.getPhotoUrl().toString();
            }else{
                imgUrl = AppConstrains.DEFAULT_URL_IMG;
            }
            loginSocial(imgUrl, account.getDisplayName(), account.getEmail(), "Google");
        } catch (ApiException e) {
            Toast.makeText(this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    // login social
    private void loginSocial(String url, String name, String email, String type){
        MultipartBody.Builder builder = new MultipartBody.Builder();
        builder.setType(MultipartBody.FORM);
        builder.addFormDataPart("name", name).addFormDataPart("pic", url)
                .addFormDataPart("email", email).addFormDataPart("src2", type);
        api.loginSocial(builder.build()).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    try {
                        JSONObject jsonObject = new JSONObject(String.valueOf(response.body()));
                        if (jsonObject.opt("id") != null) {
                            pref.setUserId(jsonObject.optString("id"));
                            if (!jsonObject.optString("state").equals("null")){
                                startActivity(new Intent(Login.this, Home.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
                            }else {
                                startActivity(new Intent(Login.this, UpdatePfInfo.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
                            }
                        } else {
                            pref.setUserId(jsonObject.optString("Data"));
                            startActivity(new Intent(Login.this, UpdatePfInfo.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
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
                Toast.makeText(Login.this, t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // login
    private void login(String email, String pw){
        RequestBody rqEmail = RequestBody.create(MediaType.parse("text/plain"), email);
        RequestBody rqPw = RequestBody.create(MediaType.parse("text/plain"), pw);
        api.login(rqEmail, rqPw).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()){
                    try {
                        JSONObject jsonObject = new JSONObject(String.valueOf(response.body()));
                        Toast.makeText(Login.this, jsonObject.optString("Message"), Toast.LENGTH_SHORT).show();
                        if (jsonObject.optBoolean("Status")){
                            startActivity(new Intent(Login.this, Home.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK));
                            JSONObject jsData = jsonObject.optJSONObject("data");
                            if (jsData != null){
                                pref.setUserId(jsData.optString("id"));
                            }
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