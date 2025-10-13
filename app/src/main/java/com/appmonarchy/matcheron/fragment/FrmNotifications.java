package com.appmonarchy.matcheron.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.appmonarchy.matcheron.R;
import com.appmonarchy.matcheron.activities.Home;
import com.appmonarchy.matcheron.adapter.NotifiAdapter;
import com.appmonarchy.matcheron.databinding.FrmNotificationsBinding;
import com.appmonarchy.matcheron.model.Notifications;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FrmNotifications extends Fragment {
    FrmNotificationsBinding binding;
    Home activity;
    List<Notifications> list;
    NotifiAdapter adapter;
    public FrmNotifications() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        activity = (Home) getActivity();
        binding = FrmNotificationsBinding.inflate(getLayoutInflater());
        init();

        return binding.getRoot();
    }

    // init UI
    private void init(){
        list = new ArrayList<>();
        adapter = new NotifiAdapter(list, activity);
        binding.rcv.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false));
        binding.rcv.setAdapter(adapter);
        activity.tool.rcvNoAnimator(binding.rcv);
        activity.tool.showLoading();
        getData();
    }

    // get data
    private void getData(){
        activity.api.getDataById("push_notifications.php", activity.pref.getUserId()).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                list.clear();
                if (response.isSuccessful()){
                    try {
                        JSONObject jsObj = new JSONObject(String.valueOf(response.body()));
                        JSONArray arrData = jsObj.optJSONArray("Data");
                        if (arrData != null && arrData.length() > 0){
                            for (int i = 0;i < arrData.length();i++){
                                JSONObject objData = (JSONObject) arrData.get(i);
                                list.add(new Notifications(objData.optString("id"), objData.optString("description"), objData.optString("seen"),
                                        objData.optString("created"), objData.optString("sid")));
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
            public void onFailure(Call<JsonObject> call, Throwable t) {
                activity.tool.hideLoading();
                binding.tvNoData.setVisibility(View.VISIBLE);
            }
        });
    }
}