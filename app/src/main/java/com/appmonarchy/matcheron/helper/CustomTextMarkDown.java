package com.appmonarchy.matcheron.helper;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;

import androidx.core.content.res.ResourcesCompat;

import com.appmonarchy.matcheron.R;


public class CustomTextMarkDown extends ClickableSpan {
    private final Runnable onClickListener;
    private final String textColor;
    private final Context context;

    public CustomTextMarkDown(Runnable onClickListener, String textColor, Context context) {
        this.onClickListener = onClickListener;
        this.textColor = textColor;
        this.context = context;
    }

    @Override
    public void onClick(View widget) {
        onClickListener.run();
    }

    @Override
    public void updateDrawState(TextPaint ds) {
        ds.setColor(Color.parseColor(textColor));
        Typeface boldFont = ResourcesCompat.getFont(context, R.font.baloo2_semibold);
        ds.setTypeface(boldFont);
    }
}