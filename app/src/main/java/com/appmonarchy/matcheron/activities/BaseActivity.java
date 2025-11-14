package com.appmonarchy.matcheron.activities;

import android.app.Dialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.appmonarchy.matcheron.R;
import com.appmonarchy.matcheron.api.APIService;
import com.appmonarchy.matcheron.api.APIUtils;
import com.appmonarchy.matcheron.databinding.PopupReportBinding;
import com.appmonarchy.matcheron.helper.AppSharedPreferences;
import com.appmonarchy.matcheron.helper.Tool;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BaseActivity extends AppCompatActivity {
    public Tool tool;
    public AppSharedPreferences pref;
    public APIService api;
    public String goalId = "";
    public GoogleSignInClient googleSignInClient;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tool = new Tool(this);
        pref = new AppSharedPreferences(this);
        api = APIUtils.getAPIService();
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        googleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    // popup report
    public void popupReport(){
        Dialog dialog = new Dialog(this);
        PopupReportBinding reportBinding = PopupReportBinding.inflate(getLayoutInflater());
        dialog.setContentView(reportBinding.getRoot());
        tool.setupDialog(dialog, Gravity.BOTTOM, ViewGroup.LayoutParams.WRAP_CONTENT);

        reportBinding.btBack.setOnClickListener(v -> dialog.dismiss());
        reportBinding.tvReason1.setOnClickListener(v -> {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
            alertDialog.setTitle(getString(R.string.app_name))
                    .setMessage(getString(R.string.thanks_for_your_report))
                    .setPositiveButton("Ok", (dialogInterface, i) -> {
                        dialogInterface.dismiss();
                        dialog.dismiss();
                    });
            AlertDialog alert = alertDialog.create();
            alert.show();
        });
        reportBinding.tvReason2.setOnClickListener(v -> reportBinding.tvReason1.performClick());
        reportBinding.tvReason3.setOnClickListener(v -> reportBinding.tvReason1.performClick());

        dialog.show();
    }

    // popup delete account
    public void popupDeleteAcc(){
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.app_name))
                .setMessage(getString(R.string.delete_acc_note))
                .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                    tool.showLoading();
                    deleteAcc();
                    dialog.dismiss();
                })
                .setNegativeButton(android.R.string.no, (dialog, which) -> dialog.dismiss())
                .show();
    }

    // popup logout
    public void popupLogout(){
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.app_name))
                .setMessage(getString(R.string.do_you_want_to_logout))
                .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                    logout();
                    dialog.dismiss();
                })
                .setNegativeButton(android.R.string.no, (dialog, which) -> dialog.dismiss())
                .show();
    }

    // logout
    public void logout(){
        LoginManager.getInstance().logOut();
        googleSignInClient.signOut();
        pref.removeValue("user_id");
        pref.removeValue("pw");
        pref.removeValue("is_saved");
        startActivity(new Intent(BaseActivity.this, GetStarted.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK));
    }

    // delete account
    public void deleteAcc(){
        RequestBody rqId = RequestBody.create(MediaType.parse("text/plain"), pref.getUserId());
        RequestBody rqPw = RequestBody.create(MediaType.parse("text/plain"), pref.getMyPf().getPw());
        api.deleteAcc(rqId, rqPw).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()){
                    try {
                        JSONObject jsonObject = new JSONObject(String.valueOf(response.body()));
                        Toast.makeText(BaseActivity.this, jsonObject.optString("Message"), Toast.LENGTH_SHORT).show();
                        if (jsonObject.optBoolean("Status")){
                            pref.removeValue("user_id");
                            pref.removeValue("pw");
                            startActivity(new Intent(BaseActivity.this, GetStarted.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK));
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
                Toast.makeText(BaseActivity.this, t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
