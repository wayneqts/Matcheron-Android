package com.appmonarchy.matcheron.fragment;

import android.os.Bundle;

import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.appmonarchy.matcheron.R;
import com.appmonarchy.matcheron.activities.Home;
import com.appmonarchy.matcheron.adapter.MixerAdapter;
import com.appmonarchy.matcheron.databinding.FrmMixersBinding;
import com.appmonarchy.matcheron.model.Event;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class FrmMixers extends Fragment {
    FrmMixersBinding binding;
    List<Event> list;
    MixerAdapter adapter;
    Home home;
    public FrmMixers() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        home = (Home) getActivity();
        binding = FrmMixersBinding.inflate(getLayoutInflater());
        init();

        return binding.getRoot();
    }

    // init UI
    private void init(){
        list = new ArrayList<>();
        binding.rcv.setLayoutManager(new LinearLayoutManager(home, LinearLayoutManager.VERTICAL, false));
        adapter = new MixerAdapter(home, list);
        binding.rcv.setAdapter(adapter);
        getEvList();
    }

    // get event list
    private void getEvList(){
        home.tool.showLoading();
        home.api.getEvents().enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                JSONObject jsonObject;
                list.clear();
                if (response.isSuccessful()){
                    try {
                        jsonObject = new JSONObject(String.valueOf(response.body()));
                        JSONArray arrData = jsonObject.optJSONArray("data");
                        if (arrData != null && arrData.length() > 0){
                            for (int i = 0;i < arrData.length();i++){
                                JSONObject jsObj = (JSONObject) arrData.get(i);
                                list.add(new Event(jsObj.optString("id"), jsObj.optString("photo"), jsObj.optString("name")));
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                adapter.notifyDataSetChanged();
                binding.tvNoData.setVisibility(adapter.getItemCount() == 0 ? View.VISIBLE : View.GONE);
                home.tool.hideLoading();
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                home.tool.hideLoading();
                Toast.makeText(home, home.getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
            }
        });
    }
}