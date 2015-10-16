package com.gmail.nelsonr462.bestie.helpers;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Build;
import android.text.SpannableStringBuilder;
import android.text.Spanned;

import com.gmail.nelsonr462.bestie.R;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;


public class FontOverride {

    public static void setDefaultFont(Context context,
                                      String staticTypefaceFieldName, String fontAssetName) {
        final Typeface regular = Typeface.createFromAsset(context.getAssets(),
                fontAssetName);
        replaceFont(staticTypefaceFieldName, regular);
    }

    protected static void replaceFont(String staticTypefaceFieldName,
                                      final Typeface newTypeface) {
        if (Build.VERSION.SDK_INT == 21) {
            Map<String, Typeface> newMap = new HashMap<String, Typeface>();
            newMap.put("sans-serif", newTypeface);
            try {
                final Field staticField = Typeface.class
                        .getDeclaredField("sSystemFontMap");
                staticField.setAccessible(true);
                staticField.set(null, newMap);
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        } else {
            try {
                final Field staticField = Typeface.class
                        .getDeclaredField(staticTypefaceFieldName);
                staticField.setAccessible(true);
                staticField.set(null, newTypeface);
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    public static SpannableStringBuilder setCustomFont(String text, Typeface customFont) {
        SpannableStringBuilder snackString = new SpannableStringBuilder(text);
        snackString.setSpan(new CustomTypefaceSpan("", customFont), 0, text.length()-1, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        return snackString;
    }
}
