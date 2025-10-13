package com.appmonarchy.matcheron.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.appmonarchy.matcheron.R;
import com.appmonarchy.matcheron.activities.EditPf;
import com.appmonarchy.matcheron.activities.Home;
import com.appmonarchy.matcheron.activities.UpdatePfInfo;
import com.appmonarchy.matcheron.activities.authentication.SignUp;
import com.appmonarchy.matcheron.adapter.CustomSpinnerAdapter;
import com.appmonarchy.matcheron.databinding.FrmProfileBinding;
import com.appmonarchy.matcheron.helper.AppConstrains;
import com.appmonarchy.matcheron.model.Goal;
import com.appmonarchy.matcheron.model.People;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.stfalcon.imageviewer.StfalconImageViewer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FrmProfile extends Fragment {
    FrmProfileBinding binding;
    Home activity;
    People myProfile;
    List<String> imgList;
    String phone = "";
    public FrmProfile() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        activity = (Home) getActivity();
        binding = FrmProfileBinding.inflate(getLayoutInflater());
        init();

        binding.btEditPf.setOnClickListener(v -> startActivityForResult(new Intent(activity, EditPf.class).putExtra("data", myProfile), AppConstrains.REFRESH_PF_CODE));
        binding.ivUser.setOnClickListener(v -> new StfalconImageViewer.Builder<>(activity, imgList, (imageView1, image) ->
                Glide.with(activity).load(image).error(R.drawable.no_avatar).centerInside().diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true).into(imageView1)).show());
        binding.tvPhone.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:"+phone));
            startActivity(intent);
        });

        return binding.getRoot();
    }

    // init UI
    private void init(){
        activity.tool.showLoading();
        getData();
    }

    // get user data
    private void getData(){
        activity.api.getDataById("profile.php", activity.pref.getUserId()).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                imgList = new ArrayList<>();
                if (response.isSuccessful()){
                    try {
                        JSONObject obj = new JSONObject(String.valueOf(response.body()));
                        if (obj.opt("id") != null){
                            if (!obj.optString("state").equals("null")){
                                phone = obj.optString("phone");
                                binding.tvName.setText(Html.fromHtml(obj.optString("fname")+" - <font color=#000000>"+obj.optString("age")+"</font>", Html.FROM_HTML_MODE_COMPACT));
                                binding.tvReligion.setText(Html.fromHtml(obj.optString("religion")+": <font color=#000000>"+obj.optString("pairing")+"</font>", Html.FROM_HTML_MODE_COMPACT));
                                binding.tvStt.setText(Html.fromHtml(obj.optString("status")+"/"+obj.optString("gender")+": <font color=#000000>Seeking "+obj.optString("seeking")+"</font>", Html.FROM_HTML_MODE_COMPACT));
                                binding.tvGoal.setText(Html.fromHtml("Matcheron: <font color=#000000>"+obj.optString("goal")+"</font>", Html.FROM_HTML_MODE_COMPACT));
                                binding.tvState.setText(Html.fromHtml("Current Country: <font color=#000000>"+obj.optString("country")+", "+obj.optString("state")+"</font>", Html.FROM_HTML_MODE_COMPACT));
                                binding.tvHeight.setText(Html.fromHtml("Height: <font color=#000000>"+obj.optString("height")+"</font>", Html.FROM_HTML_MODE_COMPACT));
                                binding.tvWeight.setText(Html.fromHtml("Weight: <font color=#000000>"+obj.optString("weight")+"lbs</font>", Html.FROM_HTML_MODE_COMPACT));
                                binding.tvProfession.setText(Html.fromHtml("Profession: <font color=#000000>"+obj.optString("profession")+"</font>", Html.FROM_HTML_MODE_COMPACT));
                                binding.tvPhone.setText(Html.fromHtml("Phone: <font color=#000000>"+obj.optString("phone")+"</font>", Html.FROM_HTML_MODE_COMPACT));
                                binding.tvBio.setText(obj.optString("bio"));
                                if (!obj.optString("origin_country").equals("null") && !obj.optString("origin_country").equals("")){
                                    binding.tvOrigin.setText(Html.fromHtml("Country of Origin: <font color=#000000>"+obj.optString("origin_country")+"</font>", Html.FROM_HTML_MODE_COMPACT));
                                    binding.llOrigin.setVisibility(View.VISIBLE);
                                }else {
                                    binding.llOrigin.setVisibility(View.GONE);
                                }
                                if (!obj.optString("img1").equals("")){
                                    imgList.add(obj.optString("img1"));
                                }
                                if (!obj.optString("img2").equals("")){
                                    imgList.add(obj.optString("img2"));
                                }
                                if (!obj.optString("img3").equals("")){
                                    imgList.add(obj.optString("img3"));
                                }
                                if (obj.optString("phonestatus").equals("") || obj.optString("phonestatus").equals("0")){
                                    binding.llPhone.setVisibility(View.GONE);
                                }else {
                                    binding.llPhone.setVisibility(View.VISIBLE);
                                }
                                myProfile = new People(obj.optString("id"), obj.optString("fname"), obj.optString("lname"), obj.optString("gender"),
                                        obj.optString("seeking"), obj.optString("age"), obj.optString("height"), obj.optString("weight"), obj.optString("status"),
                                        obj.optString("country"), obj.optString("state"), obj.optString("phone"), obj.optString("email"), obj.optString("religion"),
                                        obj.optString("goal"), obj.optString("pairing"), obj.optString("profession"), obj.optString("img1"), obj.optString("img2"),
                                        obj.optString("img3"), obj.optString("bio"), obj.optString("origin_country"), obj.optString("created"), obj.optString("phonestatus"));
                            }else {
                                startActivity(new Intent(activity, UpdatePfInfo.class));
                            }
                        }else {
                            activity.logout();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                if (imgList.size() > 0){
                    Glide.with(activity).load(imgList.get(0)).error(R.drawable.no_avatar).placeholder(R.drawable.no_avatar).centerCrop().diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true).into(binding.ivUser);
                    if (imgList.size() == 2){
                        binding.ll2Img.setVisibility(View.VISIBLE);
                    }else if (imgList.size() == 3){
                        binding.ll3Img.setVisibility(View.VISIBLE);
                    }
                }
                activity.tool.hideLoading();
                binding.llContent.setVisibility(View.VISIBLE);
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                activity.tool.hideLoading();
                binding.llContent.setVisibility(View.VISIBLE);
            }
        });
    }
}