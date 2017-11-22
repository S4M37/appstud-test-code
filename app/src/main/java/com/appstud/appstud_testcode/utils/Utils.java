package com.appstud.appstud_testcode.utils;

import com.appstud.appstud_testcode.config.Endpoints;
import com.appstud.appstud_testcode.services.GoogleApiRetrofitServices;
import com.google.gson.Gson;

import java.math.BigDecimal;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Utils {

    //TIMEOUT with Seconds
    private static final long TIMEOUT = 15;


    private static GoogleApiRetrofitServices googleApiRetrofitServices;

    public static GoogleApiRetrofitServices getGoogleApiRetrofitServices() {
        if (googleApiRetrofitServices == null) {

            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            OkHttpClient client = new OkHttpClient().newBuilder().addInterceptor(interceptor)
                    .connectTimeout(Utils.TIMEOUT, TimeUnit.SECONDS)
                    .readTimeout(Utils.TIMEOUT, TimeUnit.SECONDS).build();

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(Endpoints.GOOGLE_API_BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            googleApiRetrofitServices = retrofit.create(GoogleApiRetrofitServices.class);
        }
        return googleApiRetrofitServices;
    }

    private static Gson gson;

    public static Gson getGson() {
        if (gson == null) {
            gson = new Gson();
        }
        return gson;
    }

    public static float round(float d, int decimalPlace) {
        BigDecimal bd = new BigDecimal(Float.toString(d));
        bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);
        return bd.floatValue();
    }

}
