package com.yarolegovich.currencyexchange.net;

import android.support.annotation.Nullable;

import com.yarolegovich.currencyexchange.model.Currency;
import com.yarolegovich.currencyexchange.model.ExchangeData;
import com.yarolegovich.currencyexchange.model.ExchangeRate;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by yarolegovich on 22.02.2017.
 */
@SuppressWarnings("WeakerAccess")
abstract class Api {

    public static final String BASE_URL = "https://query.yahooapis.com/v1/public/";

    public static final String HISTORICAL = "yql?format=json&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys";

    private static DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

    private static final String FORMAT_QUERY =
            "select Date, Close, Open, Low, High from yahoo.finance.historicaldata " +
                    "where symbol = \"%s=X\"" +
                    "and startDate = \"%s\"" +
                    "and endDate = \"%s\"";

    public static String toQuery(Currency to, Date startDate, Date endDate) {
        return String.format(Locale.US, FORMAT_QUERY, to.name(),
                DATE_FORMAT.format(startDate),
                DATE_FORMAT.format(endDate));
    }

    @Nullable
    public static ExchangeData parseResponse(Currency to, String json) {
        try {
            JSONObject jObj = new JSONObject(json).getJSONObject("query");
            int count = jObj.getInt("count");
            JSONObject results = jObj.getJSONObject("results");
            List<ExchangeRate> rates;
            if (count == 0) {
                rates = Collections.emptyList();
            } else if (count == 1) {
                rates = Collections.singletonList(parseRate(results.getJSONObject("quote")));
            } else {
                rates = new ArrayList<>();
                JSONArray quote = results.getJSONArray("quote");
                for (int i = 0; i < quote.length(); i++) {
                    rates.add(parseRate(quote.getJSONObject(i)));
                }
            }
            return new ExchangeData(to, rates);
        } catch (Exception e) {
            return null;
        }
    }

    private static ExchangeRate parseRate(JSONObject jObj) throws JSONException, ParseException {
        return new ExchangeRate(
                DATE_FORMAT.parse(jObj.getString("Date")),
                jObj.getDouble("Open"),
                jObj.getDouble("Close"),
                jObj.getDouble("Low"),
                jObj.getDouble("High"));
    }

}
