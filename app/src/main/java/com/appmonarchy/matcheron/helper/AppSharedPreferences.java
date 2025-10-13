package com.appmonarchy.matcheron.helper;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;

import com.appmonarchy.matcheron.model.People;
import com.google.gson.Gson;

public class AppSharedPreferences {
    Context context;
    SharedPreferences pref;
    SharedPreferences.Editor editor;

    public AppSharedPreferences(Context context) {
        this.context = context;
    }

    // save user id
    public void setUserId(String id){
        pref = context.getSharedPreferences(AppConstrains.APP_PREF, Context.MODE_PRIVATE);
        editor = pref.edit();
        editor.putString("user_id", id).apply();
    }

    public String getUserId(){
        pref = context.getSharedPreferences(AppConstrains.APP_PREF, Context.MODE_PRIVATE);
        return pref.getString("user_id", "");
    }

    // check save token push
    public void setSavedToken(boolean b){
        pref = context.getSharedPreferences(AppConstrains.APP_PREF, Context.MODE_PRIVATE);
        editor = pref.edit();
        editor.putBoolean("is_saved", b).apply();
    }

    public boolean getSavedToken(){
        pref = context.getSharedPreferences(AppConstrains.APP_PREF, Context.MODE_PRIVATE);
        return pref.getBoolean("is_saved", false);
    }

    // save my profile
    public void setMyPf(People pf) {
        pref = context.getSharedPreferences(AppConstrains.APP_PREF, Context.MODE_PRIVATE);
        editor = pref.edit();
        Gson gson = new Gson();
        editor.putString("profile", gson.toJson(pf)).apply();
    }

    public People getMyPf() {
        pref = context.getSharedPreferences(AppConstrains.APP_PREF, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = pref.getString("profile", "");
        return gson.fromJson(json, People.class);
    }

    // remove value
    public void removeValue(String key){
        pref = context.getSharedPreferences(AppConstrains.APP_PREF, Context.MODE_PRIVATE);
        editor = pref.edit();
        editor.remove(key).apply();
    }
}
