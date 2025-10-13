package com.appmonarchy.matcheron.activities;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.appmonarchy.matcheron.R;
import com.appmonarchy.matcheron.databinding.ActivityHomeBinding;
import com.appmonarchy.matcheron.databinding.PopupReportBinding;
import com.appmonarchy.matcheron.fragment.FrmContactUs;
import com.appmonarchy.matcheron.fragment.FrmHome;
import com.appmonarchy.matcheron.fragment.FrmLiked;
import com.appmonarchy.matcheron.fragment.FrmLikes;
import com.appmonarchy.matcheron.fragment.FrmMatch;
import com.appmonarchy.matcheron.fragment.FrmMaybe;
import com.appmonarchy.matcheron.fragment.FrmMessages;
import com.appmonarchy.matcheron.fragment.FrmMixers;
import com.appmonarchy.matcheron.fragment.FrmNotifications;
import com.appmonarchy.matcheron.fragment.FrmPartner;
import com.appmonarchy.matcheron.fragment.FrmProfile;
import com.appmonarchy.matcheron.fragment.FrmSearch;
import com.appmonarchy.matcheron.fragment.FrmTerm;
import com.appmonarchy.matcheron.helper.AppConstrains;
import com.appmonarchy.matcheron.model.People;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Home extends BaseActivity {
    public ActivityHomeBinding binding;
    FragmentManager frmManager;
    String crFrm;
    int doubleBackToExit = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        init();

        binding.btToolbar.setOnClickListener(v -> binding.drawer.openDrawer(GravityCompat.START));
        binding.mnQa.setOnClickListener(v -> {
            binding.drawer.closeDrawer(GravityCompat.START);
            new Handler().postDelayed(() -> tool.openWeb(AppConstrains.QA_URL), 200);
        });
        binding.mnDeleteAcc.setOnClickListener(v -> popupDeleteAcc());
        binding.mnLogout.setOnClickListener(v -> popupLogout());
        binding.mnHome.setOnClickListener(v -> {
            binding.drawer.closeDrawer(GravityCompat.START);
            new Handler().postDelayed(() -> {
                if (!crFrm.equals("home")){
                    binding.ivHome.setVisibility(View.VISIBLE);
                    binding.btReport.setVisibility(View.VISIBLE);
                    binding.tvTitle.setVisibility(View.GONE);
                    replaceFrm(new FrmHome(), "home");
                }
            }, 200);
        });
        binding.mnPf.setOnClickListener(v -> {
            binding.drawer.closeDrawer(GravityCompat.START);
            new Handler().postDelayed(() -> {
                if (!crFrm.equals("profile")){
                    clickMnWithOutTitle();
                    replaceFrm(new FrmProfile(), "profile");
                }
            }, 200);
        });
        binding.mnMatch.setOnClickListener(v -> {
            binding.drawer.closeDrawer(GravityCompat.START);
            new Handler().postDelayed(() -> {
                if (!crFrm.equals("match")){
                    clickMnWithOutTitle();
                    replaceFrm(new FrmMatch(), "match");
                }
            }, 200);
        });
        binding.mnPartner.setOnClickListener(v -> {
            binding.drawer.closeDrawer(GravityCompat.START);
            new Handler().postDelayed(() -> {
                if (!crFrm.equals("partner")){
                    clickMnWithOutTitle();
                    replaceFrm(new FrmPartner(), "partner");
                }
            }, 200);
        });
        binding.mnLikes.setOnClickListener(v -> {
            binding.drawer.closeDrawer(GravityCompat.START);
            new Handler().postDelayed(() -> {
                if (!crFrm.equals("likes")){
                    clickMnWithTitle(getString(R.string.likes));
                    replaceFrm(new FrmLikes(), "likes");
                }
            }, 200);
        });
        binding.mnLiked.setOnClickListener(v -> {
            binding.drawer.closeDrawer(GravityCompat.START);
            new Handler().postDelayed(() -> {
                if (!crFrm.equals("liked")){
                    clickMnWithTitle(getString(R.string.liked));
                    replaceFrm(new FrmLiked(), "liked");
                }
            }, 200);
        });
        binding.mnMess.setOnClickListener(v -> {
            binding.drawer.closeDrawer(GravityCompat.START);
            new Handler().postDelayed(() -> {
                if (!crFrm.equals("message")){
                    clickMnWithTitle(getString(R.string.messages));
                    replaceFrm(new FrmMessages(), "message");
                }
            }, 200);
        });
        binding.mnSearch.setOnClickListener(v -> {
            binding.drawer.closeDrawer(GravityCompat.START);
            new Handler().postDelayed(() -> {
                if (!crFrm.equals("search")){
                    clickMnWithTitle(getString(R.string.search_title));
                    replaceFrm(new FrmSearch(), "search");
                }
            }, 200);
        });
        binding.mnTerm.setOnClickListener(v -> {
            binding.drawer.closeDrawer(GravityCompat.START);
            new Handler().postDelayed(() -> {
                if (!crFrm.equals("term")){
                    clickMnWithTitle(getString(R.string.terms));
                    replaceFrm(new FrmTerm(), "term");
                }
            }, 200);
        });
        binding.mnMixer.setOnClickListener(v -> {
            binding.drawer.closeDrawer(GravityCompat.START);
            new Handler().postDelayed(() -> {
                if (!crFrm.equals("mixer")){
                    clickMnWithTitle(getString(R.string.mixer_events));
                    replaceFrm(new FrmMixers(), "mixer");
                }
            }, 200);
        });
        binding.mnContact.setOnClickListener(v -> {
            binding.drawer.closeDrawer(GravityCompat.START);
            new Handler().postDelayed(() -> {
                if (!crFrm.equals("contact")){
                    clickMnWithTitle(getString(R.string.contact_us));
                    replaceFrm(new FrmContactUs(), "contact");
                }
            }, 300);
        });
        binding.mnMaybe.setOnClickListener(v -> {
            binding.drawer.closeDrawer(GravityCompat.START);
            new Handler().postDelayed(() -> {
                if (!crFrm.equals("maybe")){
                    clickMnWithTitle(getString(R.string.maybe));
                    binding.btReport.setVisibility(View.VISIBLE);
                    replaceFrm(new FrmMaybe(), "maybe");
                }
            }, 300);
        });
        binding.mnNotifi.setOnClickListener(v -> {
            binding.drawer.closeDrawer(GravityCompat.START);
            new Handler().postDelayed(() -> {
                if (!crFrm.equals("notifi")){
                    clickMnWithTitle(getString(R.string.notification));
                    replaceFrm(new FrmNotifications(), "notifi");
                }
            }, 300);
        });
        binding.btReport.setOnClickListener(v -> popupReport());
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (binding.drawer.isDrawerOpen(GravityCompat.START)) {
                    binding.drawer.closeDrawer(GravityCompat.START);
                } else {
                    if (doubleBackToExit == 2) {
                        finishAffinity();
                        System.exit(0);
                    } else {
                        doubleBackToExit++;
                        Toast.makeText(Home.this, R.string.please_press_back_again_to_exit, Toast.LENGTH_SHORT).show();
                    }
                    new Handler().postDelayed(() -> doubleBackToExit = 1, 2000);
                }
            }
        };
        this.getOnBackPressedDispatcher().addCallback(this, callback);
    }

    // init UI
    private void init(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
            }
        }
        frmManager = getSupportFragmentManager();
        Calendar c = Calendar.getInstance();
        binding.tvAppDes.setText("@ "+c.get(Calendar.YEAR)+" Matcheron by App\nMonarchy, Inc. All rights reserved.");
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, binding.drawer, binding.toolBar, R.string.drawer_open, R.string.drawer_close);
        binding.drawer.addDrawerListener(toggle);
        toggle.syncState();
        replaceFrm(new FrmHome(), "home");
        getData();
        if (!pref.getSavedToken()){
            FirebaseMessaging.getInstance().getToken()
                    .addOnCompleteListener(task -> {
                        if (!task.isSuccessful()) {
                            Log.w("TAG", "Fetching FCM registration token failed", task.getException());
                            return;
                        }
                        String token = task.getResult();
                        updateToken(token);
                    });
        }
    }

    // request permission
    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (!isGranted) {
                    Toast.makeText(this, getString(R.string.permission_notifications_denied), Toast.LENGTH_SHORT).show();
                }
            });

    // replace fragment
    public void replaceFrm(Fragment frm, String tag){
        frmManager.beginTransaction().replace(R.id.fl_main, frm).commit();
        crFrm = tag;
    }

    // click menu with title
    private void clickMnWithTitle(String title){
        binding.ivHome.setVisibility(View.GONE);
        binding.btReport.setVisibility(View.GONE);
        binding.tvTitle.setVisibility(View.VISIBLE);
        binding.tvTitle.setText(title);
    }

    // click menu without title
    private void clickMnWithOutTitle(){
        binding.ivHome.setVisibility(View.GONE);
        binding.btReport.setVisibility(View.GONE);
        binding.tvTitle.setVisibility(View.GONE);
    }

    // update imei
    private void updateToken(String token){
        MultipartBody.Builder builder = new MultipartBody.Builder();
        builder.setType(MultipartBody.FORM);
        builder.addFormDataPart("id", pref.getUserId()).addFormDataPart("imei", token);
        api.updateImei(builder.build()).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                updateEmei();
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {

            }
        });
    }

    // update emei
    private void updateEmei(){
        MultipartBody.Builder builder = new MultipartBody.Builder();
        builder.setType(MultipartBody.FORM);
        builder.addFormDataPart("id", pref.getUserId());
        api.updateEmei(builder.build()).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                pref.setSavedToken(true);
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {

            }
        });
    }

    // get user data
    private void getData(){
        api.getDataById("profile.php", pref.getUserId()).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()){
                    try {
                        JSONObject obj = new JSONObject(String.valueOf(response.body()));
                        if (obj.opt("id") != null){
                            if (!obj.optString("state").equals("null")){
                                People myProfile = new People(obj.optString("id"), obj.optString("fname"), obj.optString("lname"), obj.optString("gender"),
                                        obj.optString("seeking"), obj.optString("age"), obj.optString("height"), obj.optString("weight"), obj.optString("status"),
                                        obj.optString("country"), obj.optString("state"), obj.optString("phone"), obj.optString("email"), obj.optString("religion"),
                                        obj.optString("goal"), obj.optString("pairing"), obj.optString("profession"), obj.optString("img1"), obj.optString("img2"),
                                        obj.optString("img3"), obj.optString("bio"), obj.optString("origin_country"), obj.optString("created"), obj.optString("phonestatus"));
                                myProfile.setPw(obj.optString("password"));
                                pref.setMyPf(myProfile);
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == AppConstrains.REFRESH_PF_CODE){
            replaceFrm(new FrmProfile(), "profile");
        }
        if (resultCode == AppConstrains.REFRESH_MESS_CODE){
            replaceFrm(new FrmMessages(), "message");
        }
        if (resultCode == AppConstrains.REFRESH_MAYBE_CODE){
            replaceFrm(new FrmLikes(), "likes");
        }
    }
}