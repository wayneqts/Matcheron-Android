package com.appmonarchy.matcheron.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.appmonarchy.matcheron.R;
import com.appmonarchy.matcheron.activities.authentication.Login;
import com.appmonarchy.matcheron.activities.authentication.SignUp;
import com.appmonarchy.matcheron.adapter.GridAdapter;
import com.appmonarchy.matcheron.databinding.ActivityGetStartedBinding;
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

public class GetStarted extends BaseActivity {
    ActivityGetStartedBinding binding;
    List<People> list;
    GridAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityGetStartedBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        init();

        binding.btLogin.setOnClickListener(v -> startActivity(new Intent(this, Login.class)));
        binding.btJoin.setOnClickListener(v -> startActivity(new Intent(this, SignUp.class)));
    }

    // init UI
    private void init(){
        list = new ArrayList<>();
        binding.rcv.setLayoutManager(new GridLayoutManager(this, 2));
        adapter = new GridAdapter(this, list, false);
        binding.rcv.setAdapter(adapter);
        getData();
    }

    // get data
    private void getData(){
        api.getUserList().enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()){
                    try {
                        JSONObject jsObj = new JSONObject(String.valueOf(response.body()));
                        JSONArray arrMess = jsObj.optJSONArray("Message");
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
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                adapter.notifyDataSetChanged();
                binding.pbLoading.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                binding.pbLoading.setVisibility(View.GONE);
            }
        });
    }
}