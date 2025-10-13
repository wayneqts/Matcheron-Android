package com.appmonarchy.matcheron.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.appmonarchy.matcheron.R;
import com.appmonarchy.matcheron.adapter.GuestAdapter;
import com.appmonarchy.matcheron.databinding.ActivityEventDetailBinding;
import com.appmonarchy.matcheron.model.Event;
import com.bumptech.glide.Glide;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EventDetail extends BaseActivity {
    ActivityEventDetailBinding binding;
    List<Event> list;
    GuestAdapter adapter;
    String eventUrl = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEventDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        init();

        binding.btBack.setOnClickListener(v -> finish());
        binding.btShare.setOnClickListener(v -> {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Check this out!");
            shareIntent.putExtra(Intent.EXTRA_TEXT, eventUrl);
            startActivity(Intent.createChooser(shareIntent, "Share via"));
        });
        binding.btJoin.setOnClickListener(v -> {
            tool.showLoading();
            joinEv();
        });
    }

    // init UI
    private void init(){
        list = new ArrayList<>();
        binding.rcv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        adapter = new GuestAdapter(this, list);
        binding.rcv.setAdapter(adapter);
        tool.rcvNoAnimator(binding.rcv);
        tool.showLoading();
        getDetail();
    }

    // get event detail
    private void getDetail(){
        api.getDataById("event_details.php", getIntent().getStringExtra("id")).enqueue(new Callback<JsonObject>() {
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
                                Glide.with(EventDetail.this).load(jsObj.optString("photo")).centerCrop().into(binding.iv);
                                binding.tvTitle.setText(jsObj.optString("name"));
                                binding.tvTime.setText(tool.fmNewDate(jsObj.optString("date")));
                                binding.tvAddress.setText(jsObj.optString("location"));
                                binding.tvPrice.setText("Admission:"+jsObj.optString("entry_fee")+"$");
                                eventUrl = jsObj.optString("event_url");
                                JSONArray arrUser = jsObj.optJSONArray("users_joined");
                                if (arrUser != null && arrUser.length() > 0){
                                    for (int j = 0;j < arrUser.length();j++){
                                        JSONObject jsUser = (JSONObject) arrUser.get(j);
                                        list.add(new Event(jsUser.optString("uid"), jsUser.optString("img1"), jsUser.optString("fname")+" "+jsUser.optString("lname")));
                                        if (jsUser.optString("uid").equals(pref.getUserId())){
                                            binding.btJoin.setText(getString(R.string.unjoin));
                                        }
                                    }
                                }
                            }
                            binding.main.setVisibility(View.VISIBLE);
                        }else {
                            Toast.makeText(EventDetail.this, getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }else {
                    Toast.makeText(EventDetail.this, getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                    finish();
                }
                adapter.notifyDataSetChanged();
                binding.tvNoData.setVisibility(adapter.getItemCount() == 0 ? View.VISIBLE : View.GONE);
                tool.hideLoading();
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                tool.hideLoading();
                Toast.makeText(EventDetail.this, getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    // join event
    private void joinEv(){
        api.joinEv(pref.getUserId(), getIntent().getStringExtra("id")).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                JSONObject jsonObject;
                getDetail();
                if (response.isSuccessful()){
                    try {
                        jsonObject = new JSONObject(String.valueOf(response.body()));
                        if (jsonObject.optString("Message").equals("done")){
                            Toast.makeText(EventDetail.this, getString(R.string.you_have_joined_the_event), Toast.LENGTH_SHORT).show();
                        }else {
                            Toast.makeText(EventDetail.this, getString(R.string.you_have_left_event), Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                tool.hideLoading();
                Toast.makeText(EventDetail.this, getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
            }
        });
    }
}