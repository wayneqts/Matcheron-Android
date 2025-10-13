package com.appmonarchy.matcheron.activities;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.appmonarchy.matcheron.R;
import com.appmonarchy.matcheron.activities.authentication.SignUp;
import com.appmonarchy.matcheron.adapter.CustomSpinnerAdapter;
import com.appmonarchy.matcheron.adapter.GoalAdapter;
import com.appmonarchy.matcheron.databinding.ActivityUpdatePfInfoBinding;
import com.appmonarchy.matcheron.databinding.PopupAskReligionBinding;
import com.appmonarchy.matcheron.databinding.PopupCountryBinding;
import com.appmonarchy.matcheron.model.Goal;
import com.bumptech.glide.Glide;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UpdatePfInfo extends BaseActivity {
    ActivityUpdatePfInfoBinding binding;
    List<Goal> goalList, religionList, stateList, countryList;
    List<String> countryOfOriginList, searchList;
    GoalAdapter goalAdapter;
    ArrayAdapter<String> arrayAdapter;
    Boolean updatedImg1 = false, updatedImg2 = false, updatedImg3 = false;
    int SELECT_FILE = 2, CAMERA_CODE = 147, REQUEST_CAMERA = 0, imgPos;
    String religionId = "", stateId = "", gender = "Male", seeking = "Male", pairing = "Exclusive", stt = "", pw = "", countryId = "", phoneStt = "0";
    Bitmap bm1, bm2, bm3;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUpdatePfInfoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        init();

        binding.rgPairing.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rb_exclusive){
                pairing = "Exclusive";
            }else {
                pairing = "Open";
            }
        });
        binding.rgShow.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rb_hide) {
                phoneStt = "0";
            } else {
                phoneStt = "1";
            }
        });
        binding.spinnerReligion.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0){
                    ((TextView) view).setTextColor(getColor(R.color.gray1));
                }else {
                    if (!religionId.equals(religionList.get(position).getId())){
                        popupAsk(religionList.get(position));
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        binding.spinnerState.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0){
                    ((TextView) view).setTextColor(getColor(R.color.gray1));
                }else {
                    stateId = stateList.get(position).getId();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        binding.spinnerStt.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0){
                    ((TextView) view).setTextColor(getColor(R.color.gray1));
                }else {
                    String[] arr = getResources().getStringArray(R.array.status);
                    stt = arr[position];
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        binding.rgGender.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rb_male) {
                gender = "Male";
                binding.rbFemaleSeeking.setChecked(true);
            } else {
                gender = "Female";
                binding.rbMaleSeeking.setChecked(true);
            }
        });
        binding.rgSeeking.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rb_male_seeking){
                seeking = "Male";
            }else{
                seeking = "Female";
            }
        });
        binding.btChooseImg1.setOnClickListener(v -> {
            imgPos = 1;
            popupSelectPhoto();
        });
        binding.btChooseImg2.setOnClickListener(v -> {
            imgPos = 2;
            popupSelectPhoto();
        });
        binding.btChooseImg3.setOnClickListener(v -> {
            imgPos = 3;
            popupSelectPhoto();
        });
        binding.etOrigin.setOnClickListener(v -> popupCountry());
        binding.btSave.setOnClickListener(v -> {
            String fName = binding.etFName.getText().toString().trim();
            String lName = binding.etLName.getText().toString().trim();
            String age = binding.etAge.getText().toString().trim();
            String height = binding.etHeight.getText().toString().trim();
            String weight = binding.etWeight.getText().toString().trim();
            String phone = binding.etPhone.getText().toString().trim();
            String profession = binding.etProfession.getText().toString().trim();
            String bio = binding.etCmt.getText().toString().trim();
            if (TextUtils.isEmpty(fName)){
                Toast.makeText(this, getString(R.string.first_name_is_required), Toast.LENGTH_SHORT).show();
            }else if (TextUtils.isEmpty(lName)){
                Toast.makeText(this, getString(R.string.last_name_is_required), Toast.LENGTH_SHORT).show();
            }else if (religionId.equals("")){
                Toast.makeText(this, getString(R.string.religion_is_required), Toast.LENGTH_SHORT).show();
            }else if (TextUtils.isEmpty(age)){
                Toast.makeText(this, getString(R.string.age_is_required), Toast.LENGTH_SHORT).show();
            }else if (TextUtils.isEmpty(height)){
                Toast.makeText(this, getString(R.string.height_is_required), Toast.LENGTH_SHORT).show();
            }else if (TextUtils.isEmpty(weight)){
                Toast.makeText(this, getString(R.string.weight_is_required), Toast.LENGTH_SHORT).show();
            }else if (TextUtils.isEmpty(phone)){
                Toast.makeText(this, getString(R.string.mobile_number_is_required), Toast.LENGTH_SHORT).show();
            }else if (stt.equals("")){
                Toast.makeText(this, getString(R.string.status_is_required), Toast.LENGTH_SHORT).show();
            }else if (stateId.equals("")){
                Toast.makeText(this, getString(R.string.state_is_required), Toast.LENGTH_SHORT).show();
            }else if (TextUtils.isEmpty(profession)){
                Toast.makeText(this, getString(R.string.profession_is_required), Toast.LENGTH_SHORT).show();
            }else if (TextUtils.isEmpty(bio)){
                Toast.makeText(this, getString(R.string.bio_is_required), Toast.LENGTH_SHORT).show();
            }else {
                tool.showLoading();
                updatePf();
            }
        });
    }

    // init UI
    private void init(){
        tool.showLoading();
        searchList = new ArrayList<>();
        goalList = new ArrayList<>();
        goalAdapter = new GoalAdapter(goalList, this, false);
        binding.rcvGoal.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        binding.rcvGoal.setAdapter(goalAdapter);
        tool.rcvNoAnimator(binding.rcvGoal);
        CustomSpinnerAdapter sttAdapter = new CustomSpinnerAdapter(this, android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.status));
        sttAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerStt.setAdapter(sttAdapter);
        getCrInfo();
        getGoal();
        getReligion();
        getCountryOfOrigin();
        getCountry();
    }

    // popup select country
    private void popupCountry() {
        Dialog dialog = new Dialog(this);
        PopupCountryBinding countryBinding = PopupCountryBinding.inflate(getLayoutInflater());
        dialog.setContentView(countryBinding.getRoot());
        tool.setupDialog(dialog, Gravity.BOTTOM, ViewGroup.LayoutParams.MATCH_PARENT);

        countryBinding.btClose.setOnClickListener(v -> {
            getCountryOfOrigin();
            dialog.dismiss();
        });

        countryBinding.rcv.setAdapter(arrayAdapter);
        countryBinding.rcv.setOnItemClickListener((parent, view, position, id) -> {
            binding.etOrigin.setText(countryOfOriginList.get(position));
            dialog.dismiss();
        });
        countryBinding.etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0) {
                    searchList.clear();
                    for (int i = 0; i < countryOfOriginList.size(); i++) {
                        if (s.length() <= countryOfOriginList.get(i).length()) {
                            if (countryOfOriginList.get(i).toLowerCase().trim().contains(s)) {
                                searchList.add(countryOfOriginList.get(i));
                            }
                        }
                    }
                    arrayAdapter = new ArrayAdapter<>(UpdatePfInfo.this, R.layout.item_country, R.id.textView, searchList);
                    countryBinding.rcv.setAdapter(arrayAdapter);
                }else {
                    arrayAdapter = new ArrayAdapter<>(UpdatePfInfo.this, R.layout.item_country, R.id.textView, countryOfOriginList);
                    countryBinding.rcv.setAdapter(arrayAdapter);
                }
            }
        });

        dialog.show();
    }

    // popup ask religion
    private void popupAsk(Goal goal){
        Dialog dialog = new Dialog(this);
        PopupAskReligionBinding religionBinding = PopupAskReligionBinding.inflate(getLayoutInflater());
        dialog.setContentView(religionBinding.getRoot());
        dialog.setCanceledOnTouchOutside(false);
        tool.setupDialog(dialog, Gravity.CENTER, ViewGroup.LayoutParams.WRAP_CONTENT);

        religionBinding.btYes.setText("Yes, I'm "+goal.getName());
        religionBinding.btYes.setOnClickListener(v -> {
            religionId = goal.getId();
            dialog.dismiss();
        });
        religionBinding.btNo.setOnClickListener(v -> {
            if (religionId.equals("")){
                binding.spinnerReligion.setSelection(0);
            }
            dialog.dismiss();
        });

        dialog.show();
    }

    // popup select photo
    public void popupSelectPhoto(){
        CharSequence[] items = {"Take a photo", "Choose from library"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setItems(items, (dialog, item) -> {
            if (items[item].equals("Take a photo")) {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.CAMERA},
                            REQUEST_CAMERA);
                } else if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri = Uri.fromParts("package", getPackageName(), null);
                    intent.setData(uri);
                    startActivity(intent);
                } else {
                    cameraIntent();
                }
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED) {
                        galleryIntent();
                    } else if (shouldShowRequestPermissionRationale(Manifest.permission.READ_MEDIA_IMAGES)) {
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", getPackageName(), null);
                        intent.setData(uri);
                        startActivity(intent);
                    } else {
                        requestPermissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES);
                    }
                } else {
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                            || ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(this,
                                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                1);
                    } else {
                        galleryIntent();
                    }
                }
            }
        });
        builder.show();
    }

    // request permission
    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    galleryIntent();
                } else {
                    Toast.makeText(this, getString(R.string.permission_access_device_image_denied), Toast.LENGTH_SHORT).show();
                }
            });

    // select image from library
    private void galleryIntent() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);//
        startActivityForResult(Intent.createChooser(intent, "Select File"), SELECT_FILE);
    }

    public void cameraIntent() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, CAMERA_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                galleryIntent();
            } else {
                Toast.makeText(this, getString(R.string.permission_access_device_image_denied), Toast.LENGTH_SHORT).show();
            }
        }
        if (requestCode == REQUEST_CAMERA){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                cameraIntent();
            } else {
                Toast.makeText(this, getString(R.string.permission_access_camera_denied), Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK && data != null){
            if (requestCode == CAMERA_CODE){
                Bitmap bm = (Bitmap) data.getExtras().get("data");
                if (imgPos == 1){
                    bm1 = bm;
                    updatedImg1 = true;
                    Glide.with(this).load(bm).centerCrop().into(binding.iv1);
                }else if (imgPos == 2){
                    bm2 = bm;
                    updatedImg2 = true;
                    Glide.with(this).load(bm).centerCrop().into(binding.iv2);
                }else {
                    bm3 = bm;
                    updatedImg3 = true;
                    Glide.with(this).load(bm).centerCrop().into(binding.iv3);
                }
            }
            if (requestCode == SELECT_FILE){
                Uri uri = data.getData();
                try {
                    Bitmap bm = MediaStore.Images.Media.getBitmap(this. getContentResolver(), uri);
                    if (imgPos == 1){
                        bm1 = bm;
                        updatedImg1 = true;
                        Glide.with(this).load(bm).centerCrop().into(binding.iv1);
                    }else if (imgPos == 2){
                        bm2 = bm;
                        updatedImg2 = true;
                        Glide.with(this).load(bm).centerCrop().into(binding.iv2);
                    }else {
                        bm3 = bm;
                        updatedImg3 = true;
                        Glide.with(this).load(bm).centerCrop().into(binding.iv3);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // get country
    private void getCountry() {
        api.getCountry().enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                JSONObject jsonObject;
                if (response.isSuccessful()){
                    try {
                        jsonObject = new JSONObject(String.valueOf(response.body()));
                        binding.etCountry.setText(jsonObject.optString("currentcountry"));
                        countryId = jsonObject.optString("currentid");
                        getState(countryId);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(UpdatePfInfo.this, t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // update profile
    private void updatePf(){
        String fName = binding.etFName.getText().toString().trim();
        String lName = binding.etLName.getText().toString().trim();
        String age = binding.etAge.getText().toString().trim();
        String height = binding.etHeight.getText().toString().trim();
        String weight = binding.etWeight.getText().toString().trim();
        String email = binding.etEmail.getText().toString().trim();
        String phone = binding.etPhone.getText().toString().trim();
        String profession = binding.etProfession.getText().toString().trim();
        String bio = binding.etCmt.getText().toString().trim();
        String origin = binding.etOrigin.getText().toString().trim();
        MultipartBody.Builder builder = new MultipartBody.Builder();
        builder.setType(MultipartBody.FORM);
        builder.addFormDataPart("fname", fName).addFormDataPart("lname", lName).addFormDataPart("gender", gender)
                .addFormDataPart("seeking", seeking).addFormDataPart("age", age).addFormDataPart("height", height)
                .addFormDataPart("weight", weight).addFormDataPart("status", stt).addFormDataPart("country", countryId)
                .addFormDataPart("state", stateId).addFormDataPart("phone", phone).addFormDataPart("email", email)
                .addFormDataPart("religion", religionId).addFormDataPart("goal", goalId).addFormDataPart("pairing", pairing)
                .addFormDataPart("password", pw).addFormDataPart("profession", profession).addFormDataPart("bio", bio)
                .addFormDataPart("id", pref.getUserId()).addFormDataPart("origin_country", origin).addFormDataPart("phonestatus", phoneStt);
        if (updatedImg1){
            RequestBody rqFile = RequestBody.create(MediaType.parse("multipart/form-data"), tool.getFileFromBm(bm1));
            builder.addFormDataPart("img1", tool.getFileFromBm(bm1).getName(), rqFile);
        }
        if (updatedImg2){
            RequestBody rqFile = RequestBody.create(MediaType.parse("multipart/form-data"), tool.getFileFromBm(bm2));
            builder.addFormDataPart("img2", tool.getFileFromBm(bm2).getName(), rqFile);
        }
        if (updatedImg3){
            RequestBody rqFile = RequestBody.create(MediaType.parse("multipart/form-data"), tool.getFileFromBm(bm3));
            builder.addFormDataPart("img3", tool.getFileFromBm(bm3).getName(), rqFile);
        }
        api.updatePf(builder.build()).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()){
                    try {
                        JSONObject jsObj = new JSONObject(String.valueOf(response.body()));
                        if (jsObj.optBoolean("Status")){
                            Toast.makeText(UpdatePfInfo.this, jsObj.optString("Message"), Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(UpdatePfInfo.this, Home.class));
                            finish();
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
                Toast.makeText(UpdatePfInfo.this, t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // get goal
    private void getGoal(){
        api.getDataArr("goals.php").enqueue(new Callback<JsonArray>() {
            @Override
            public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
                if (response.isSuccessful()){
                    try {
                        JSONArray jsArr = new JSONArray(String.valueOf(response.body()));
                        for (int i = 0;i < jsArr.length();i++){
                            JSONObject obj = (JSONObject) jsArr.get(i);
                            goalList.add(new Goal(obj.optString("id"), obj.optString("name")));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                goalAdapter.notifyDataSetChanged();
                binding.pbGoal.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<JsonArray> call, Throwable t) {
                binding.pbGoal.setVisibility(View.GONE);
                Toast.makeText(UpdatePfInfo.this, t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // get religion
    private void getReligion(){
        api.getDataArr("religion.php").enqueue(new Callback<JsonArray>() {
            @Override
            public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
                religionList = new ArrayList<>();
                String[] religionArr = null;
                if (response.isSuccessful()){
                    try {
                        JSONArray jsArr = new JSONArray(String.valueOf(response.body()));
                        religionList.add(new Goal("0", "Select Religion"));
                        religionArr = new String[jsArr.length() + 1];
                        religionArr[0] = "Select Religion";
                        for (int i = 0;i < jsArr.length();i++){
                            JSONObject obj = (JSONObject) jsArr.get(i);
                            religionList.add(new Goal(obj.optString("id"), obj.optString("name")));
                            religionArr[i + 1] = obj.optString("name");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                if (religionArr != null){
                    CustomSpinnerAdapter religionAdapter = new CustomSpinnerAdapter(UpdatePfInfo.this, android.R.layout.simple_spinner_item, religionArr);
                    religionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    binding.spinnerReligion.setAdapter(religionAdapter);
                }
                binding.pbReligion.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<JsonArray> call, Throwable t) {
                binding.pbReligion.setVisibility(View.GONE);
                Toast.makeText(UpdatePfInfo.this, t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // get state
    private void getState(String id) {
        binding.spinnerState.setVisibility(View.GONE);
        binding.ivDown.setVisibility(View.GONE);
        binding.pbState.setVisibility(View.VISIBLE);
        api.getArrById("state.php", id).enqueue(new Callback<JsonArray>() {
            @Override
            public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
                String[] stateArr = null;
                stateList = new ArrayList<>();
                if (response.isSuccessful()) {
                    try {
                        JSONArray jsArr = new JSONArray(String.valueOf(response.body()));
                        stateList.add(new Goal("0", "State/Province"));
                        stateArr = new String[jsArr.length() + 1];
                        stateArr[0] = "State/Province";
                        for (int i = 0; i < jsArr.length(); i++) {
                            JSONObject obj = (JSONObject) jsArr.get(i);
                            stateList.add(new Goal(obj.optString("id"), obj.optString("name")));
                            stateArr[i + 1] = obj.optString("name");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                if (stateArr != null) {
                    CustomSpinnerAdapter religionAdapter = new CustomSpinnerAdapter(UpdatePfInfo.this, android.R.layout.simple_spinner_item, stateArr);
                    religionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    binding.spinnerState.setAdapter(religionAdapter);
                }
                binding.spinnerState.setVisibility(View.VISIBLE);
                binding.ivDown.setVisibility(View.VISIBLE);
                binding.pbState.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<JsonArray> call, Throwable t) {
                Toast.makeText(UpdatePfInfo.this, t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // get countries
    private void getCountryOfOrigin(){
        countryOfOriginList = new ArrayList<>();
        try {
            JSONArray jsArr = new JSONArray(tool.loadJSONFromAsset());
            for (int i = 0; i < jsArr.length(); i++) {
                JSONObject obj = (JSONObject) jsArr.get(i);
                countryOfOriginList.add(obj.optString("country"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        arrayAdapter = new ArrayAdapter<>(this, R.layout.item_country, R.id.textView, countryOfOriginList);
    }

    // get user data
    private void getCrInfo(){
        api.getDataById("profile.php", pref.getUserId()).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()){
                    try {
                        JSONObject obj = new JSONObject(String.valueOf(response.body()));
                        binding.etFName.setText(obj.optString("fname"));
                        binding.etEmail.setText(obj.optString("email"));
                        Glide.with(UpdatePfInfo.this).load(obj.optString("img1")).error(R.drawable.no_avatar).centerCrop().into(binding.iv1);
                        Glide.with(UpdatePfInfo.this).load(obj.optString("img2")).error(R.drawable.no_avatar).centerCrop().into(binding.iv2);
                        Glide.with(UpdatePfInfo.this).load(obj.optString("img3")).error(R.drawable.no_avatar).centerCrop().into(binding.iv3);
                        pw = obj.optString("password");
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