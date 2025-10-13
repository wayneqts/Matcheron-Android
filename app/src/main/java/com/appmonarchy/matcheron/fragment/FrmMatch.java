package com.appmonarchy.matcheron.fragment;

import android.app.Dialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.appmonarchy.matcheron.R;
import com.appmonarchy.matcheron.activities.Home;
import com.appmonarchy.matcheron.databinding.FrmMatchBinding;
import com.appmonarchy.matcheron.databinding.PopupSubscribeBinding;


public class FrmMatch extends Fragment {
    FrmMatchBinding binding;
    Home home;
    private boolean isFormatting;
    private int lastStart = 0;
    private String lastBefore = "";

    private boolean isFormat; // tránh loop
    private int selectionIndexBefore;
    PopupSubscribeBinding subBinding;

    public FrmMatch() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        home = (Home) getActivity();
        binding = FrmMatchBinding.inflate(getLayoutInflater());

        binding.btSub.setOnClickListener(v -> popupSub());

        return binding.getRoot();
    }

    // popup subscribe
    private void popupSub(){
        Dialog dialog = new Dialog(home);
        subBinding = PopupSubscribeBinding.inflate(getLayoutInflater());
        dialog.setContentView(subBinding.getRoot());
        home.tool.setupDialog(dialog, Gravity.CENTER, ViewGroup.LayoutParams.WRAP_CONTENT);

        subBinding.btCancel.setOnClickListener(v -> dialog.dismiss());
        subBinding.etNum.addTextChangedListener(cardTextWatcher);
        subBinding.etDate.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (!isFormat) {
                    selectionIndexBefore = subBinding.etDate.getSelectionStart();
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (isFormat) return;
                isFormat = true;

                String original = s.toString();
                int cursorPos = subBinding.etDate.getSelectionStart();

                String digits = original.replaceAll("\\D", "");

                if (digits.length() > 4) digits = digits.substring(0, 4);

                StringBuilder formatted = new StringBuilder();
                for (int i = 0; i < digits.length(); i++) {
                    if (i == 2) formatted.append('/');
                    formatted.append(digits.charAt(i));
                }

                String newText = formatted.toString();

                int newCursorPos = cursorPos;

                if (cursorPos == original.length()) {
                    newCursorPos = newText.length();
                } else {
                    int digitsBeforeCursor = countDigitsBeforePosition(original, cursorPos);
                    if (digitsBeforeCursor <= 2) {
                        newCursorPos = digitsBeforeCursor;
                    } else {
                        newCursorPos = digitsBeforeCursor + 1;
                    }
                    if (newCursorPos > newText.length()) newCursorPos = newText.length();
                    if (newCursorPos < 0) newCursorPos = 0;
                }

                if (!newText.equals(original)) {
                    subBinding.etDate.setText(newText);
                }
                try {
                    subBinding.etDate.setSelection(newCursorPos);
                } catch (IndexOutOfBoundsException e) {
                    subBinding.etDate.setSelection(newText.length());
                }

                isFormat = false;
            }
        });

        dialog.show();
    }

    private static int countDigitsBeforePosition(String text, int pos) {
        pos = Math.max(0, Math.min(pos, text.length()));
        int count = 0;
        for (int i = 0; i < pos; i++) {
            if (Character.isDigit(text.charAt(i))) count++;
        }
        return count;
    }

    private final TextWatcher cardTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            if (!isFormatting) {
                lastStart = start;
                lastBefore = s.toString();
            }
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) { }

        @Override
        public void afterTextChanged(Editable s) {
            if (isFormatting) return;
            isFormatting = true;

            String digitsOnly = getDigitsOnly(s.toString());
            // Limit to 16 digits for typical Visa (Visa can be 13/16/19 but most common 16)
            if (digitsOnly.length() > 16) {
                digitsOnly = digitsOnly.substring(0, 16);
            }

            // Format groups of 4: "XXXX XXXX XXXX XXXX"
            String formatted = formatCardNumber(digitsOnly);

            subBinding.etNum.setText(formatted);
            // restore cursor to the end (or smarter position)
            subBinding.etNum.setSelection(formatted.length());

            isFormatting = false;
        }
    };

    // remove non-digits
    private static String getDigitsOnly(String s) {
        return s.replaceAll("\\D", "");
    }

    // insert space every 4 digits
    private static String formatCardNumber(String digits) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < digits.length(); i++) {
            if (i > 0 && i % 4 == 0) sb.append(' ');
            sb.append(digits.charAt(i));
        }
        return sb.toString();
    }
}