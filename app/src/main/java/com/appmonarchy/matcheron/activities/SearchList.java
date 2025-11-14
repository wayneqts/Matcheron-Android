package com.appmonarchy.matcheron.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import android.os.Bundle;
import android.view.View;

import com.appmonarchy.matcheron.R;
import com.appmonarchy.matcheron.adapter.HomePeopleAdapter;
import com.appmonarchy.matcheron.databinding.ActivitySearchListBinding;
import com.appmonarchy.matcheron.model.People;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchList extends BaseActivity {
    ActivitySearchListBinding binding;
    List<People> list;
    HomePeopleAdapter homePeopleAdapter;
    LinearLayoutManager layoutManager;
    int crPos = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySearchListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        init();

        binding.rcv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == 0){
                    crPos = layoutManager.findLastVisibleItemPosition();
                }
            }
        });
        binding.btNext.setOnClickListener(v -> {
            if (crPos < list.size() - 1){
                crPos++;
                binding.rcv.smoothScrollToPosition(crPos);
            }
        });
        binding.btPre.setOnClickListener(v -> {
            if (crPos > 0){
                crPos--;
                binding.rcv.smoothScrollToPosition(crPos);
            }
        });
        binding.btMaybe.setOnClickListener(v -> {
            tool.showLoading();
            addOpt("3");
        });
        binding.btNo.setOnClickListener(v -> {
            tool.showLoading();
            addOpt("2");
        });
        binding.btYes.setOnClickListener(v -> {
            tool.showLoading();
            addOpt("1");
        });
        binding.btBack.setOnClickListener(v -> finish());
        binding.btReport.setOnClickListener(v -> popupReport());
    }

    // init Ui
    private void init(){
        list = new ArrayList<>();
        homePeopleAdapter = new HomePeopleAdapter(list, this);
        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        binding.rcv.setLayoutManager(layoutManager);
        SnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(binding.rcv);
        binding.rcv.setAdapter(homePeopleAdapter);
        tool.showLoading();
        getData();
    }

    // get data
    private void getData(){
        api.getSearchList(pref.getUserId(), getIntent().getStringExtra("f_name")).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                list.clear();
                if (response.isSuccessful()){
                    try {
                        JSONObject jsonObject = new JSONObject(String.valueOf(response.body()));
                        if (jsonObject.optBoolean("Status")){
                            JSONArray arrMess = jsonObject.optJSONArray("Message");
                            if (arrMess != null && arrMess.length() > 0){
                                for (int i = 0;i < arrMess.length();i++){
                                    JSONObject obj = (JSONObject) arrMess.get(i);
                                    list.add(new People(obj.optString("id"), obj.optString("fname"), obj.optString("lname"), obj.optString("gender"),
                                            obj.optString("seeking"), obj.optString("age"), obj.optString("height"), obj.optString("weight"), obj.optString("status"),
                                            obj.optString("country"), obj.optString("state"), obj.optString("phone"), obj.optString("email"), obj.optString("religion"),
                                            obj.optString("goal"), obj.optString("pairing"), obj.optString("profession"), obj.optString("img1"), obj.optString("img2"),
                                            obj.optString("img3"), obj.optString("bio"), obj.optString("origin_country"), obj.optString("created"), obj.optString("phonestatus")));
                                }
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                homePeopleAdapter.notifyDataSetChanged();
                tool.hideLoading();
                binding.tvNoData.setVisibility(list.size() == 0 ? View.VISIBLE : View.GONE);
                binding.rlContent.setVisibility(list.size() == 0 ? View.GONE : View.VISIBLE);
                binding.llBt.setVisibility(list.size() == 0 ? View.GONE : View.VISIBLE);
                binding.btReport.setVisibility(list.size() == 0 ? View.GONE : View.VISIBLE);
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                tool.hideLoading();
            }
        });
    }

    // add people option
    private void addOpt(String type){
        api.addPeopleOpt(pref.getUserId(), list.get(crPos).getId(), type).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()){
                    list.remove(crPos);
                    homePeopleAdapter.notifyItemRemoved(crPos);
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