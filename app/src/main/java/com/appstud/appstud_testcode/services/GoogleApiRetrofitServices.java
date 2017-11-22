package com.appstud.appstud_testcode.services;


import com.appstud.appstud_testcode.config.Endpoints;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface GoogleApiRetrofitServices {
    @GET(Endpoints.GOOGLE_NEARBYSEARCH)
    Call<ResponseBody> nearbySearch(@Query("key") String key, @Query("location") String location,
                                    @Query("radius") int radius, @Query("type") String type,
                                    @Query("keyword") String keyword);

}
