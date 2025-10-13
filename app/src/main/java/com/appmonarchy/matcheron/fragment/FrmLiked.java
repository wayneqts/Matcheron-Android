package com.appmonarchy.matcheron.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.appmonarchy.matcheron.R;
import com.appmonarchy.matcheron.activities.Home;
import com.appmonarchy.matcheron.adapter.GridAdapter;
import com.appmonarchy.matcheron.databinding.FrmLikedBinding;
import com.appmonarchy.matcheron.model.People;
import com.google.gson.JsonArray;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FrmLiked extends Fragment {
    FrmLikedBinding binding;
    Home activity;
    List<People> list;
    GridAdapter adapter;
    public FrmLiked() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        activity = (Home) getActivity();
        binding = FrmLikedBinding.inflate(getLayoutInflater());
        init();

        return binding.getRoot();
    }

    // init UI
    private void init(){
        list = new ArrayList<>();
        binding.rcv.setLayoutManager(new GridLayoutManager(activity, 2));
        adapter = new GridAdapter(activity, list, true);
        binding.rcv.setAdapter(adapter);
        getData();
        activity.tool.showLoading();
    }

    // get data
    private void getData(){
        activity.api.getArrById("likedme.php", activity.pref.getUserId()).enqueue(new Callback<JsonArray>() {
            @Override
            public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
                list.clear();
                if (response.isSuccessful()){
                    try {
                        JSONArray jsArr = new JSONArray(String.valueOf(response.body()));
                        if (jsArr.length() > 0){
                            for (int i = 0;i < jsArr.length();i++){
                                JSONObject obj = (JSONObject) jsArr.get(i);
                                list.add(new People(obj.optString("id"), obj.optString("fname"), obj.optString("lname"), obj.optString("gender"),
                                        obj.optString("seeking"), obj.optString("age"), obj.optString("height"), obj.optString("weight"), obj.optString("status"),
                                        obj.optString("country"), obj.optString("state"), obj.optString("phone"), obj.optString("email"), obj.optString("religion"),
                                        obj.optString("goal"), obj.optString("pairing"), obj.optString("profession"), obj.optString("img1"), obj.optString("img2"),
                                        obj.optString("img3"), obj.optString("bio"), obj.optString("origin_country"), obj.optString("created"), obj.optString("phonestatus")));
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                adapter.notifyDataSetChanged();
                activity.tool.hideLoading();
                binding.tvNoData.setVisibility(list.size() == 0 ? View.VISIBLE : View.GONE);
            }

            @Override
            public void onFailure(Call<JsonArray> call, Throwable t) {
                activity.tool.hideLoading();
                binding.tvNoData.setVisibility(View.VISIBLE);
            }
        });
    }
}