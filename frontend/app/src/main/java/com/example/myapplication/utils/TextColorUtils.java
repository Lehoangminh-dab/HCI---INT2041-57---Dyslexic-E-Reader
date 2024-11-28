package com.example.myapplication.utils;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.example.myapplication.R;
import com.example.myapplication.model.ColorRule;

import java.util.List;

public class TextColorUtils {
    public static SpannableString applyColorToText(Context context, String text, List<ColorRule> rules) {
        SpannableString spannableString = new SpannableString(text);

        // Cần tìm data chứa các từ có digraphs
        String[] digraphs = {"ch", "sh", "th", "wh", "ph"};

        boolean isStartOfWord = true;

        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);

            for (int j = 0; j < rules.size(); j++) {
                if (!rules.get(j).isDefault()) {
                    String condition = rules.get(j).getName();
                    int color = rules.get(j).getColor();
                    if (condition.length() == 1) {
                        condition = condition.toUpperCase() + condition;
                        if (condition.indexOf(c) != -1) {
                            spannableString.setSpan(new ForegroundColorSpan(color), i, i + 1,
                                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        }
                    }
                    else {
                        if (i <= text.length() - condition.length() && text.substring(i, i + condition.length()).equalsIgnoreCase(condition)) {
                            spannableString.setSpan(new ForegroundColorSpan(color), i, i + condition.length(),
                                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                            i += condition.length() - 1;
                        }
                    }
                }
            }


            if (isStartOfWord && Character.isLetter(c)) {
                spannableString.setSpan(new ForegroundColorSpan(ContextCompat.getColor(context, R.color.pink)), i, i + 1,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                isStartOfWord = false;
            }

            if (Character.isWhitespace(c)) {
                isStartOfWord = true;
            }

            if (i < text.length() - 1) {
                String pair = text.substring(i, i + 2).toLowerCase();
                for (String digraph : digraphs) {
                    if (pair.equals(digraph)) {
                        spannableString.setSpan(new ForegroundColorSpan(ContextCompat.getColor(context, R.color.blue)), i, i + 2,
                                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        i++;
                        break;
                    }
                }
            }

            if (i < text.length() - 1 && "AEIOUaeiou".indexOf(c) != -1 &&
                    "rR".indexOf(text.charAt(i + 1)) != -1) {
                spannableString.setSpan(new ForegroundColorSpan(ContextCompat.getColor(context, R.color.olive)), i, i + 2,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            else if ("AEIOUaeiou".indexOf(c) != -1) {
                spannableString.setSpan(new ForegroundColorSpan(ContextCompat.getColor(context, R.color.green)), i, i + 1,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            else if (i < text.length() - 1 && "aiayeeoo".indexOf(c) != -1 &&
                    "aeiou".indexOf(text.charAt(i + 1)) != -1) {
                spannableString.setSpan(new ForegroundColorSpan(ContextCompat.getColor(context, R.color.olive)), i, i + 2,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                i++;
            }
            else if ("Bb".indexOf(c) != -1) {
                spannableString.setSpan(new ForegroundColorSpan(ContextCompat.getColor(context, R.color.red)), i, i + 1,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }

        }

        return spannableString;
    }
}

