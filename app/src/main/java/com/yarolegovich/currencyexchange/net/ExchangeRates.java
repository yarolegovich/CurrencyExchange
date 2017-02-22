package com.yarolegovich.currencyexchange.net;

import android.support.annotation.Nullable;
import android.util.Log;

import com.yarolegovich.currencyexchange.model.Currency;
import com.yarolegovich.currencyexchange.model.ExchangeData;
import com.yarolegovich.currencyexchange.model.ExchangeRate;
import com.yarolegovich.currencyexchange.util.Logger;

import org.json.JSONObject;

import java.io.IOException;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by yarolegovich on 22.02.2017.
 */

public class ExchangeRates {

    public static ExchangeRates create() {
        return new ExchangeRates();
    }

    private ResultListener resultListener;
    private ErrorListener errorListener;

    private ExchangeRates() {
    }

    public void getHistoricalRates(Currency to, Date startDate, Date endDate) {
        String query = Api.toQuery(to, startDate, endDate);
        Call<ResponseBody> call = JsonRatesService.INSTANCE.getHistoricalData(query);
        call.enqueue(new CallResultDispatcher(to));
    }

    public void setResultListener(ResultListener resultListener) {
        this.resultListener = resultListener;
    }

    public void setErrorListener(ErrorListener errorListener) {
        this.errorListener = errorListener;
    }

    private class CallResultDispatcher implements Callback<ResponseBody> {

        private Currency to;

        private CallResultDispatcher(Currency to) {
            this.to = to;
        }

        @Override
        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
            try {
                if (response.isSuccessful()) {
                    ExchangeData result = Api.parseResponse(to, response.body().string());
                    if (result != null) {
                        Logger.d(result.toString());
                        if (resultListener != null) {
                            resultListener.onExchangeDataReady(result);
                        }
                    } else {
                        notifyError(new NullPointerException("Failed to parse the response"));
                    }
                } else {
                    notifyError(new HttpError(response.message()));
                }
            } catch (Exception e) {
                notifyError(e);
            }
        }

        @Override
        public void onFailure(Call<ResponseBody> call, Throwable t) {
            notifyError(t);
        }

        private void notifyError(Throwable t) {
            Logger.e(t);
            if (errorListener != null) {
                errorListener.onError(t);
            }
        }
    }

    public interface ResultListener {
        void onExchangeDataReady(ExchangeData data);
    }

    public interface ErrorListener {
        void onError(Throwable e);
    }
}
