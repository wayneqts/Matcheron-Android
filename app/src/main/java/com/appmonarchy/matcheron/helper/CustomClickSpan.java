package com.appmonarchy.matcheron.helper;

import android.graphics.Color;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;

public class CustomClickSpan extends ClickableSpan {
    private final Runnable onClickListener;
    private final String textColor;

    public CustomClickSpan(Runnable onClickListener, String textColor) {
        this.onClickListener = onClickListener;
        this.textColor = textColor;
    }

    @Override
    public void onClick(View widget) {
        onClickListener.run();
    }

    @Override
    public void updateDrawState(TextPaint ds) {
        ds.setColor(Color.parseColor(textColor));
    }
}