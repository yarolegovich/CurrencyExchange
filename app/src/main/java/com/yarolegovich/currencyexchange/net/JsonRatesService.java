package com.yarolegovich.currencyexchange.net;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by yarolegovich on 22.02.2017.
 */

interface JsonRatesService {

    JsonRatesService INSTANCE = new Retrofit.Builder()
            .baseUrl(Api.BASE_URL)
            .build()
            .create(JsonRatesService.class);

    @GET(Api.HISTORICAL)
    Call<ResponseBody> getHistoricalData(@Query("q") String yqlQuery);
}
