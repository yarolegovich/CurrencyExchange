package com.yarolegovich.currencyexchange.util;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.widget.TextView;

import java.util.Locale;

/**
 * Created by yarolegovich on 22.02.2017.
 */

public class Utils {

    private Context context;

    public Utils(Context context) {
        this.context = context;
    }

    @ColorInt
    public int color(@ColorRes int res) {
        return ContextCompat.getColor(context, res);
    }

    public Drawable drawable(@DrawableRes int res) {
        return ContextCompat.getDrawable(context, res);
    }

    public static String format(double f) {
        return String.format(Locale.US, "%.4f", f);
    }

    public static void tintDrawables(TextView tv, @ColorInt int color) {
        Drawable[] drawables = tv.getCompoundDrawables();
        for (int i = 0; i < drawables.length; i++) {
            if (drawables[i] != null) {
                drawables[i] = drawables[i].mutate();
                DrawableCompat.setTint(drawables[i], color);
            }
        }
        tv.setCompoundDrawables(drawables[0], drawables[1], drawables[2], drawables[3]);
    }

}
