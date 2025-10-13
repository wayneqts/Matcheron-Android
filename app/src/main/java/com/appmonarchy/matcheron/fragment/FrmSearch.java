package com.appmonarchy.matcheron.fragment;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.appmonarchy.matcheron.R;
import com.appmonarchy.matcheron.activities.Home;
import com.appmonarchy.matcheron.activities.SearchList;
import com.appmonarchy.matcheron.activities.authentication.SignUp;
import com.appmonarchy.matcheron.adapter.CustomSpinnerAdapter;
import com.appmonarchy.matcheron.adapter.GoalAdapter;
import com.appmonarchy.matcheron.databinding.FrmSearchBinding;
import com.appmonarchy.matcheron.databinding.PopupCountryBinding;
import com.appmonarchy.matcheron.model.Goal;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FrmSearch extends Fragment {
    FrmSearchBinding binding;
    Home activity;
    List<Goal> goalList, religionList, stateList, countryList;
    List<String> countryOfOriginList, searchList;
    GoalAdapter goalAdapter;
    ArrayAdapter<String> arrayAdapter;
    String religionId = "", stateId = "", countryId = "", seeking = "", pairing = "";
    public FrmSearch() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        activity = (Home) getActivity();
        binding = FrmSearchBinding.inflate(getLayoutInflater());
        init();

        binding.spinnerReligion.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0){
                    ((TextView) view).setTextColor(activity.getColor(R.color.gray1));
                }else {
                    religionId = religionList.get(position).getId();
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
                    ((TextView) view).setTextColor(activity.getColor(R.color.gray1));
                }else {
                    stateId = stateList.get(position).getId();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        binding.rgSeeking.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rb_male_seeking){
                seeking = "Male";
            }else{
                seeking = "Female";
            }
        });
        binding.rgPairing.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rb_exclusive){
                pairing = "Exclusive";
            }else {
                pairing = "Open";
            }
        });
        binding.spinnerCountry.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    ((TextView) view).setTextColor(activity.getColor(R.color.gray1));
                } else {
                    countryId = countryList.get(position).getId();
                    getState(countryId);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        binding.btSubmit.setOnClickListener(v -> {
            activity.tool.showLoading();
            setSearch();
        });
        binding.etOrigin.setOnClickListener(v -> popupCountry());
        if (activity.pref.getMyPf().getGender().equals("Male")){
            binding.rbFemaleSeeking.setChecked(true);
        }else {
            binding.rbMaleSeeking.setChecked(true);
        }

        return binding.getRoot();
    }

    // init UI
    private void init(){
        goalList = new ArrayList<>();
        goalAdapter = new GoalAdapter(goalList, activity, false);
        binding.rcvGoal.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false));
        binding.rcvGoal.setAdapter(goalAdapter);
        activity.tool.rcvNoAnimator(binding.rcvGoal);
        String[] stateArr = new String[1];
        stateArr[0] = "State/Province";
        CustomSpinnerAdapter religionAdapter = new CustomSpinnerAdapter(activity, android.R.layout.simple_spinner_item, stateArr);
        religionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerState.setAdapter(religionAdapter);
        getGoal();
        getReligion();
        getCountryOfOrigin();
        getCountry();
    }

    // popup select country
    private void popupCountry() {
        Dialog dialog = new Dialog(activity);
        PopupCountryBinding countryBinding = PopupCountryBinding.inflate(getLayoutInflater());
        dialog.setContentView(countryBinding.getRoot());
        activity.tool.setupDialog(dialog, Gravity.BOTTOM, ViewGroup.LayoutParams.MATCH_PARENT);

        countryBinding.btClose.setOnClickListener(v -> {
            getCountryOfOrigin();
            dialog.dismiss();
        });

        countryBinding.rcv.setAdapter(arrayAdapter);
        countryBinding.rcv.setOnItemClickListener((parent, view, position, id) -> {
            if (position == 0){
                binding.etOrigin.setText("");
            }else {
                binding.etOrigin.setText(countryOfOriginList.get(position));
            }
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
                    arrayAdapter = new ArrayAdapter<>(activity, R.layout.item_country, R.id.textView, searchList);
                    countryBinding.rcv.setAdapter(arrayAdapter);
                }else {
                    arrayAdapter = new ArrayAdapter<>(activity, R.layout.item_country, R.id.textView, countryOfOriginList);
                    countryBinding.rcv.setAdapter(arrayAdapter);
                }
            }
        });

        dialog.show();
    }

    // get countries
    private void getCountryOfOrigin() {
        countryOfOriginList = new ArrayList<>();
        countryOfOriginList.add("Select Country");
        try {
            JSONArray jsArr = new JSONArray(activity.tool.loadJSONFromAsset());
            for (int i = 0; i < jsArr.length(); i++) {
                JSONObject obj = (JSONObject) jsArr.get(i);
                countryOfOriginList.add(obj.optString("country"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        arrayAdapter = new ArrayAdapter<>(activity, R.layout.item_country, R.id.textView, countryOfOriginList);
    }

    // get country
    private void getCountry() {
        activity.api.getDataArr("countries.php").enqueue(new Callback<JsonArray>() {
            @Override
            public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
                String[] countryArr = null;
                countryList = new ArrayList<>();
                if (response.isSuccessful()) {
                    try {
                        JSONArray jsArr = new JSONArray(String.valueOf(response.body()));
                        countryList.add(new Goal("0", "Current Country"));
                        countryArr = new String[jsArr.length() + 1];
                        countryArr[0] = "Current Country";
                        for (int i = 0; i < jsArr.length(); i++) {
                            JSONObject obj = (JSONObject) jsArr.get(i);
                            countryList.add(new Goal(obj.optString("id"), obj.optString("country_name")));
                            countryArr[i + 1] = obj.optString("country_name");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                if (countryArr != null) {
                    CustomSpinnerAdapter countryAdapter = new CustomSpinnerAdapter(activity, android.R.layout.simple_spinner_item, countryArr);
                    countryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    binding.spinnerCountry.setAdapter(countryAdapter);
                }
                binding.pbCountry.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<JsonArray> call, Throwable t) {
                Toast.makeText(activity, t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // get goal
    private void getGoal(){
        activity.api.getDataArr("goals.php").enqueue(new Callback<JsonArray>() {
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
                goalAdapter.selectedPosition = -1;
                goalAdapter.notifyDataSetChanged();
                binding.pbGoal.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<JsonArray> call, Throwable t) {
                binding.pbGoal.setVisibility(View.GONE);
                Toast.makeText(activity, t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // get religion
    private void getReligion(){
        activity.api.getDataArr("religion.php").enqueue(new Callback<JsonArray>() {
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
                    CustomSpinnerAdapter religionAdapter = new CustomSpinnerAdapter(activity, android.R.layout.simple_spinner_item, religionArr);
                    religionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    binding.spinnerReligion.setAdapter(religionAdapter);
                }
                binding.pbReligion.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<JsonArray> call, Throwable t) {
                binding.pbReligion.setVisibility(View.GONE);
                Toast.makeText(activity, t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // get state
    private void getState(String id) {
        binding.spinnerState.setVisibility(View.GONE);
        binding.ivDown.setVisibility(View.GONE);
        binding.pbState.setVisibility(View.VISIBLE);
        activity.api.getArrById("state.php", id).enqueue(new Callback<JsonArray>() {
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
                    CustomSpinnerAdapter religionAdapter = new CustomSpinnerAdapter(activity, android.R.layout.simple_spinner_item, stateArr);
                    religionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    binding.spinnerState.setAdapter(religionAdapter);
                }
                binding.spinnerState.setVisibility(View.VISIBLE);
                binding.ivDown.setVisibility(View.VISIBLE);
                binding.pbState.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<JsonArray> call, Throwable t) {
                Toast.makeText(activity, t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // set search
    private void setSearch(){
        RequestBody rqId = RequestBody.create(MediaType.parse("text/plain"), activity.pref.getUserId());
        RequestBody rqAge1 = RequestBody.create(MediaType.parse("text/plain"), binding.etFromAge.getText().toString().trim());
        RequestBody rqAge2 = RequestBody.create(MediaType.parse("text/plain"), binding.etToAge.getText().toString().trim());
        RequestBody rqHeight1 = RequestBody.create(MediaType.parse("text/plain"), binding.etFromHeight.getText().toString().trim());
        RequestBody rqHeight2 = RequestBody.create(MediaType.parse("text/plain"), binding.etToHeight.getText().toString().trim());
        RequestBody rqWeight1 = RequestBody.create(MediaType.parse("text/plain"), binding.etFromWeight.getText().toString().trim());
        RequestBody rqWeight2 = RequestBody.create(MediaType.parse("text/plain"), binding.etToWeight.getText().toString().trim());
        RequestBody rqSeeking = RequestBody.create(MediaType.parse("text/plain"), seeking);
        RequestBody rqReligion = RequestBody.create(MediaType.parse("text/plain"), religionId);
        RequestBody rqPairing = RequestBody.create(MediaType.parse("text/plain"), pairing);
        RequestBody rqGoal = RequestBody.create(MediaType.parse("text/plain"), activity.goalId);
        RequestBody rqState = RequestBody.create(MediaType.parse("text/plain"), stateId);
        RequestBody rqCountry = RequestBody.create(MediaType.parse("text/plain"), countryId);
        RequestBody rqOrigin = RequestBody.create(MediaType.parse("text/plain"), binding.etOrigin.getText().toString().trim());
        activity.api.setSearch(rqId, rqSeeking, rqAge1, rqAge2, rqHeight1, rqHeight2, rqWeight1, rqWeight2, rqCountry, rqState, rqReligion, rqGoal, rqPairing, rqOrigin).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()){
                    startActivity(new Intent(activity, SearchList.class));
                }
                activity.tool.hideLoading();
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {

            }
        });
    }
}