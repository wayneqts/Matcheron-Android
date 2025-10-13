package com.appmonarchy.matcheron.helper;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.Annotation;
import android.text.SpannableString;
import android.text.SpannedString;
import android.text.method.LinkMovementMethod;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;


import com.appmonarchy.matcheron.activities.PrivacyPolicy;
import com.appmonarchy.matcheron.R;
import com.appmonarchy.matcheron.activities.Terms;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Tool {
    Context context;
    Dialog dialog;

    public Tool(Context context) {
        this.context = context;
    }

    // show loading
    public void showLoading() {
        dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.pb_loading);
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }

    // hide loading
    public void hideLoading() {
        if (dialog != null) {
            dialog.dismiss();
        }
    }

    // setup dialog
    public void setupDialog(Dialog dialog, int gravity, int height) {
        dialog.getWindow().setGravity(gravity);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, height);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }

    // check email valid
    public boolean isEmailValid(String email) {
        boolean isValid = false;

        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        CharSequence inputStr = email;

        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inputStr);
        if (matcher.matches()) {
            isValid = true;
        }
        return isValid;
    }

    // format to new datetime
    public Date stringToDate(String dateTime) {
        Date date = new Date();
        SimpleDateFormat fm = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
        try {
            date = fm.parse(dateTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    // format to new datetime
    public String fmNewDate(String dateTime) {
        String dayFm = "";
        SimpleDateFormat fm = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        SimpleDateFormat fmNew = new SimpleDateFormat("MMM dd, yyyy - EEEE", Locale.ENGLISH);
        try {
            Date newDate = fm.parse(dateTime);
            dayFm = fmNew.format(newDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return dayFm;
    }

    // hide keyboard
    public void hideKeyboard(View view) {
        view.requestFocus();
        InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    // recyclerview no blink
    public void rcvNoAnimator(RecyclerView rcv) {
        RecyclerView.ItemAnimator animator = rcv.getItemAnimator();
        if (animator instanceof SimpleItemAnimator) {
            ((SimpleItemAnimator) animator).setSupportsChangeAnimations(false);
        }
        rcv.getItemAnimator().setChangeDuration(0);
    }

    // disable button
    public void disableBt(View v) {
        v.setEnabled(false);
        v.setAlpha(.5f);
    }

    // enable button
    public void enableBt(View v) {
        v.setEnabled(true);
        v.setAlpha(1);
    }

    // disable radio group
    public void disableRg(RadioGroup rg){
        for (int i = 0; i < rg.getChildCount(); i++) {
            rg.getChildAt(i).setEnabled(false);
        }
        rg.setAlpha(.5f);
    }

    // enable radio group
    public void enableRg(RadioGroup rg){
        for (int i = 0; i < rg.getChildCount(); i++) {
            rg.getChildAt(i).setEnabled(true);
        }
        rg.setAlpha(1);
    }

    // get width of screen
    public int getWidthScreen() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.widthPixels;
    }

    // get height of screen
    public int getHeightScreen() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.heightPixels;
    }

    // textview mark down
    public void tvMarkDown(int res, TextView tv){
        SpannedString termsText = (SpannedString) context.getText(res);
        Annotation[] annotations = termsText.getSpans(0, termsText.length(), Annotation.class);
        SpannableString termsCopy = new SpannableString(termsText);
        for (Annotation annotation : annotations) {
            if (annotation.getKey().equals("action")) {
                termsCopy.setSpan(
                        createClickSpan(annotation.getValue()),
                        termsText.getSpanStart(annotation),
                        termsText.getSpanEnd(annotation),
                        SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE
                );
            }
        }
        tv.setText(termsCopy);
        tv.setMovementMethod(LinkMovementMethod.getInstance());
    }

    // create link span
    private CustomClickSpan createClickSpan(String action) {
        switch (action.toLowerCase()) {
            case "privacy":
                return new CustomClickSpan(() -> context.startActivity(new Intent(context, PrivacyPolicy.class)), "#F07381");
            case "term":
                return new CustomClickSpan(() -> context.startActivity(new Intent(context, Terms.class)), "#F07381");
            default:
                throw new UnsupportedOperationException("action " + action + " not implemented");
        }
    }

    // convert bitmap to file
    public File getFileFromBm(Bitmap bm){
        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), bm, "IMG_" + System.currentTimeMillis(), null);
        Uri uri = Uri.parse(path);
        String realUri = RealPathUtil.getRealPath(context, uri);
        return new File(realUri);
    }

    public File getFileFromUri(Uri uri) {
        String[] filePathColumn = { MediaStore.Images.Media.DATA };
        Cursor cursor = context.getContentResolver().query(uri, filePathColumn, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String filePath = cursor.getString(columnIndex);
            cursor.close();
            return filePath != null ? new File(filePath) : null;
        }
        return null;
    }

    // open web browser
    public void openWeb(String url){
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        context.startActivity(i);
    }

    // format time ago
    public String fmTimeAgo(String time){
        return new FmTimeAgo().covertTimeToText(time);
    }

    // read data from json file
    public String loadJSONFromAsset() {
        String json;
        try {
            InputStream is = context.getAssets().open("country.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }
}
