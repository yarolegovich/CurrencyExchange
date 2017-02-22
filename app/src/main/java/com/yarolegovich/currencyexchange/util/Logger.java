package com.yarolegovich.currencyexchange.util;

import android.util.Log;

/**
 * Created by yarolegovich on 22.02.2017.
 */

public class Logger {

    private static final boolean LOG_ON = true;

    private static final String LOG_TAG = "Int20h";

    public static void d(String format, Object ...args) {
        if (LOG_ON) {
            Log.d(LOG_TAG, String.format(format, args));
        }
    }

    public static void e(Throwable e) {
        if (LOG_ON) {
            Log.e(LOG_TAG, e.getMessage(), e);
        }
    }
}
