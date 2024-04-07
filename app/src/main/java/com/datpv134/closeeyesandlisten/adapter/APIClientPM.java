package com.datpv134.closeeyesandlisten.adapter;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class APIClientPM {
    static String BASE_URL = "https://demo6781563.mockable.io/";
    static IService iService;
    Retrofit retrofit = null;

    public static IService getInstance() {
        if (iService == null) {
            OkHttpClient okHttpClient = new OkHttpClient();
            Retrofit restAdapter = newRetrofitInstance(BASE_URL, okHttpClient);
            iService = restAdapter.create(IService.class);
        }
        return iService;
    }

    private static Retrofit newRetrofitInstance(String endPoint, OkHttpClient okHttpClient) {
        return new Retrofit.Builder()
                .baseUrl(endPoint)
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build();
    }
}
