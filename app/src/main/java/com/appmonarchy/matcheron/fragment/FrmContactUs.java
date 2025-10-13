package com.appmonarchy.matcheron.fragment;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.appmonarchy.matcheron.R;
import com.appmonarchy.matcheron.activities.Home;
import com.appmonarchy.matcheron.databinding.FrmContactUsBinding;
import com.appmonarchy.matcheron.helper.RealPathUtil;
import com.appmonarchy.matcheron.model.People;
import com.bumptech.glide.Glide;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class FrmContactUs extends Fragment {
    FrmContactUsBinding binding;
    Home activity;
    String src = "", name, email, phone, realUri;
    public FrmContactUs() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        activity = (Home) getActivity();
        binding = FrmContactUsBinding.inflate(getLayoutInflater());
        getData();

        binding.btChooseFile.setOnClickListener(v -> {
            Intent chooseFile = new Intent(Intent.ACTION_GET_CONTENT);
            chooseFile.setType("*/*");
            chooseFile = Intent.createChooser(chooseFile, "Choose a file");
            startActivityForResult(chooseFile, 123);
        });
        binding.btDelete.setOnClickListener(v -> {
            binding.btChooseFile.setText(activity.getString(R.string.choose_file));
            binding.btDelete.setVisibility(View.GONE);
        });
        binding.btSend.setOnClickListener(v -> {
            String msg = binding.etMess.getText().toString().trim();
            if (TextUtils.isEmpty(msg)){
                Toast.makeText(activity, activity.getString(R.string.message_is_required), Toast.LENGTH_SHORT).show();
            }else {
                activity.tool.showLoading();
                contact();
            }
        });

        return binding.getRoot();
    }

    // contact
    private void contact(){
        MultipartBody.Builder builder = new MultipartBody.Builder();
        builder.setType(MultipartBody.FORM);
        builder.addFormDataPart("name", name);
        builder.addFormDataPart("phone", phone);
        builder.addFormDataPart("email", email);
        builder.addFormDataPart("message", binding.etMess.getText().toString().trim());
        if (!src.equals("")){
            File file = new File(realUri);
            RequestBody rqFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
            builder.addFormDataPart("file", file.getName(), rqFile);
        }
        activity.api.contact(builder.build()).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()){
                    try {
                        JSONObject jsObj = new JSONObject(String.valueOf(response.body()));
                        if (jsObj.optBoolean("Status")){
                            Toast.makeText(activity, jsObj.optString("Message"), Toast.LENGTH_SHORT).show();
                            binding.etMess.setText("");
                            binding.etMess.clearFocus();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                activity.tool.hideLoading();
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                activity.tool.hideLoading();
            }
        });
    }

    // get user data
    private void getData(){
        activity.api.getDataById("profile.php", activity.pref.getUserId()).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()){
                    try {
                        JSONObject jsObj = new JSONObject(String.valueOf(response.body()));
                        if (!jsObj.optString("country").equals("null")){
                            name = jsObj.optString("fname");
                            email = jsObj.optString("email");
                            phone = jsObj.optString("phone");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                activity.tool.hideLoading();
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                activity.tool.hideLoading();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 123 && resultCode == Activity.RESULT_OK){
            Uri uri = data.getData();
            realUri = RealPathUtil.getRealPath(activity, uri);
            src = uri.getPath();
            binding.btChooseFile.setText("file:/"+src);
            binding.btDelete.setVisibility(View.VISIBLE);
        }
    }
}