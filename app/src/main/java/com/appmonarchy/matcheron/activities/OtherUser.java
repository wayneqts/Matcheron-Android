package com.appmonarchy.matcheron.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.appmonarchy.matcheron.R;
import com.appmonarchy.matcheron.databinding.ActivityOtherUserBinding;
import com.appmonarchy.matcheron.databinding.PopupSendMessBinding;
import com.appmonarchy.matcheron.helper.AppConstrains;
import com.appmonarchy.matcheron.model.People;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.gson.JsonObject;
import com.stfalcon.imageviewer.StfalconImageViewer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OtherUser extends BaseActivity {
    ActivityOtherUserBinding binding;
    String userId, username, blockId, userUrl = "", phone = "";
    Dialog dialog;
    List<String> imgList;
    boolean isRefresh = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOtherUserBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        init();

        binding.btBack.setOnClickListener(v -> {
            if (isRefresh){
                setResult(AppConstrains.REFRESH_MAYBE_CODE);
            }
            finish();
        });
        binding.btBlock.setOnClickListener(v -> popupBlock());
        binding.btSendMsg.setOnClickListener(v -> popupSendMess());
        binding.btOpt.setOnClickListener(v -> {
            if (getIntent().getStringExtra("from") != null){
                popupOtpLikes();
            }else {
                popupOtpUser();
            }
        });
        binding.ivUser.setOnClickListener(v -> new StfalconImageViewer.Builder<>(this, imgList, (imageView1, image) ->
                Glide.with(this).load(image).error(R.drawable.no_avatar).centerInside().diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true).into(imageView1)).show());
    }

    // init UI
    private void init(){
        tool.disableBt(binding.btBlock);
        imgList = new ArrayList<>();
        tool.showLoading();
        getData(getIntent().getStringExtra("id"));
        checkBlock();
    }

    // popup option from likes page
    private void popupOtpLikes(){
        CharSequence[] items = {"Copy Profile Url", "Report", "Move to Maybe", "Move to No/Delete"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setItems(items, (dialog, item) -> {
            if (items[item].equals("Report")) {
                popupReport();
            } else if (items[item].equals("Copy Profile Url")){
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                Uri copyUri = Uri.parse(userUrl);
                ClipData clip = ClipData.newUri(getContentResolver(), "URI", copyUri);
                clipboard.setPrimaryClip(clip);
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
                    Toast.makeText(this, getString(R.string.copied), Toast.LENGTH_SHORT).show();
                }
            }else if (items[item].equals("Move to Maybe")){
                addOpt("3");
            }else {
                addOpt("2");
            }
        });
        builder.show();
    }

    // popup option user
    private void popupOtpUser(){
        CharSequence[] items = {"Copy Profile Url", "Report"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setItems(items, (dialog, item) -> {
            if (items[item].equals("Report")) {
                popupReport();
            } else{
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                Uri copyUri = Uri.parse(userUrl);
                ClipData clip = ClipData.newUri(getContentResolver(), "URI", copyUri);
                clipboard.setPrimaryClip(clip);
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
                    Toast.makeText(this, getString(R.string.copied), Toast.LENGTH_SHORT).show();
                }
            }
        });
        builder.show();
    }

    // popup block
    private void popupBlock(){
        String method = binding.tvBlock.getText().toString().toLowerCase();
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.app_name))
                .setMessage("Are you sure you want to "+method+" this user?")
                .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                    if (method.equals("block")){
                        blockUser();
                        binding.tvBlock.setText(getString(R.string.unblock));
                    }else {
                        unBlock();
                        binding.tvBlock.setText(getString(R.string.block));
                    }
                    dialog.dismiss();
                })
                .setNegativeButton(android.R.string.no, (dialog, which) -> dialog.dismiss())
                .show();
    }

    // popup send message
    private void popupSendMess(){
        dialog = new Dialog(this);
        PopupSendMessBinding sendMsgBinding = PopupSendMessBinding.inflate(getLayoutInflater());
        dialog.setContentView(sendMsgBinding.getRoot());
        tool.setupDialog(dialog, Gravity.CENTER, ViewGroup.LayoutParams.WRAP_CONTENT);

        sendMsgBinding.tvUsername.setText(username+" ✨");
        sendMsgBinding.btClose.setOnClickListener(v -> dialog.dismiss());
        sendMsgBinding.btSendMsg.setOnClickListener(v -> {
            String mess = sendMsgBinding.etMess.getText().toString().trim();
            if (TextUtils.isEmpty(mess)){
                Toast.makeText(this, getString(R.string.message_is_required), Toast.LENGTH_SHORT).show();
            }else {
                tool.showLoading();
                sendMess(mess);
            }
        });

        dialog.show();
    }

    // send message
    private void sendMess(String mess){
        MultipartBody.Builder builder = new MultipartBody.Builder();
        builder.setType(MultipartBody.FORM);
        builder.addFormDataPart("sid", pref.getUserId()).addFormDataPart("rid", userId);
        builder.addFormDataPart("message", mess);
        api.sendMess(builder.build()).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                tool.hideLoading();
                dialog.dismiss();
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {

            }
        });
    }

    // get user data
    private void getData(String id){
        api.getDataById("profile.php", id).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()){
                    try {
                        JSONObject jsObj = new JSONObject(String.valueOf(response.body()));
                        if (!jsObj.optString("country").equals("null")){
                            phone = jsObj.optString("phone");
                            userId = jsObj.optString("id");
                            username = jsObj.optString("fname");
                            userUrl = jsObj.optString("profileur");
                            Calendar c = Calendar.getInstance();
                            c.setTime(tool.stringToDate(jsObj.optString("created")));
                            c.add(Calendar.DAY_OF_MONTH, 2);
                            if (new Date().after(c.getTime())){
                                if (jsObj.optString("gender").equals("Male")){
                                    binding.tvName.setText(Html.fromHtml(username+" - <font color=#000000>"+jsObj.optString("age")+"</font> \uD83E\uDD85", Html.FROM_HTML_MODE_COMPACT));
                                }else if (jsObj.optString("gender").equals("Female")){
                                    binding.tvName.setText(Html.fromHtml(username+" - <font color=#000000>"+jsObj.optString("age")+"</font> \uD83E\uDD8B", Html.FROM_HTML_MODE_COMPACT));
                                }else {
                                    binding.tvName.setText(Html.fromHtml(username+" - <font color=#000000>"+jsObj.optString("age")+"</font>", Html.FROM_HTML_MODE_COMPACT));
                                }
                            }else {
                                binding.tvName.setText(Html.fromHtml(username+" - <font color=#000000>"+jsObj.optString("age")+"</font>", Html.FROM_HTML_MODE_COMPACT));
                            }
                            binding.tvReligion.setText(Html.fromHtml(jsObj.optString("religion")+": <font color=#000000>"+jsObj.optString("pairing")+"</font>", Html.FROM_HTML_MODE_COMPACT));
                            binding.tvStt.setText(Html.fromHtml(jsObj.optString("status")+"/"+jsObj.optString("gender")+": <font color=#000000>Seeking "+jsObj.optString("seeking")+"</font>", Html.FROM_HTML_MODE_COMPACT));
                            binding.tvGoal.setText(Html.fromHtml("Matcheron: <font color=#000000>"+jsObj.optString("goal")+"</font>", Html.FROM_HTML_MODE_COMPACT));
                            binding.tvState.setText(Html.fromHtml("Current Country: <font color=#000000>"+jsObj.optString("country")+", "+jsObj.optString("state")+"</font>", Html.FROM_HTML_MODE_COMPACT));
                            binding.tvHeight.setText(Html.fromHtml("Height: <font color=#000000>"+jsObj.optString("height")+"</font>", Html.FROM_HTML_MODE_COMPACT));
                            binding.tvWeight.setText(Html.fromHtml("Weight: <font color=#000000>"+jsObj.optString("weight")+"lbs</font>", Html.FROM_HTML_MODE_COMPACT));
                            binding.tvProfession.setText(Html.fromHtml("Profession: <font color=#000000>"+jsObj.optString("profession")+"</font>", Html.FROM_HTML_MODE_COMPACT));
                            binding.tvPhone.setText(Html.fromHtml("Phone: <font color=#000000>"+jsObj.optString("phone")+"</font>", Html.FROM_HTML_MODE_COMPACT));
                            binding.tvBio.setText(jsObj.optString("bio"));
                            if (!jsObj.optString("img1").equals("")){
                                imgList.add(jsObj.optString("img1"));
                            }
                            if (!jsObj.optString("img2").equals("")){
                                imgList.add(jsObj.optString("img2"));
                            }
                            if (!jsObj.optString("img3").equals("")){
                                imgList.add(jsObj.optString("img3"));
                            }
                            if (!jsObj.optString("origin_country").equals("null") && !jsObj.optString("origin_country").equals("")){
                                binding.tvOrigin.setText(Html.fromHtml("Country of Origin: <font color=#000000>"+jsObj.optString("origin_country")+"</font>", Html.FROM_HTML_MODE_COMPACT));
                                binding.llOrigin.setVisibility(View.VISIBLE);
                            }else {
                                binding.llOrigin.setVisibility(View.GONE);
                            }
                            if (jsObj.optString("phonestatus").equals("") || jsObj.optString("phonestatus").equals("0")){
                                binding.llPhone.setVisibility(View.GONE);
                            }else {
                                binding.llPhone.setVisibility(View.VISIBLE);
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                if (imgList.size() > 0){
                    Glide.with(OtherUser.this).load(imgList.get(0)).error(R.drawable.no_avatar).centerCrop().into(binding.ivUser);
                    if (imgList.size() == 2){
                        binding.ll2Img.setVisibility(View.VISIBLE);
                    }else if (imgList.size() == 3){
                        binding.ll3Img.setVisibility(View.VISIBLE);
                    }
                }
                binding.nsvContent.setVisibility(View.VISIBLE);
                tool.hideLoading();
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                tool.hideLoading();
            }
        });
    }

    // check block list
    private void checkBlock(){
        api.getDataById("block_list.php", pref.getUserId()).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()){
                    try {
                        JSONObject jsObj = new JSONObject(String.valueOf(response.body()));
                        JSONArray arrData = jsObj.optJSONArray("data");
                        if (arrData != null && arrData.length() > 0){
                            for (int i = 0;i < arrData.length();i++){
                                JSONObject objData = (JSONObject) arrData.get(i);
                                if (objData.optString("blocked").equals(userId)){
                                    binding.tvBlock.setText(getString(R.string.unblock));
                                    blockId = objData.optString("id");
                                }
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                binding.pbLoading.setVisibility(View.GONE);
                binding.llBt.setVisibility(View.VISIBLE);
                tool.enableBt(binding.btBlock);
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {

            }
        });
    }

    // block user
    private void blockUser(){
        MultipartBody.Builder builder = new MultipartBody.Builder();
        builder.setType(MultipartBody.FORM);
        builder.addFormDataPart("blockby", pref.getUserId());
        builder.addFormDataPart("blocked", userId);
        api.blockUser(builder.build()).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {

            }
        });
    }

    // add people option
    private void addOpt(String type){
        tool.showLoading();
        api.addPeopleOpt(pref.getUserId(), userId, type).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()){
                    isRefresh = true;
                    Toast.makeText(OtherUser.this, "Successfully", Toast.LENGTH_SHORT).show();
                }
                tool.hideLoading();
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                tool.hideLoading();
                Toast.makeText(OtherUser.this, t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // unblock user
    private void unBlock(){
        api.getDataById("remove_block.php", blockId).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {

            }
        });
    }
}