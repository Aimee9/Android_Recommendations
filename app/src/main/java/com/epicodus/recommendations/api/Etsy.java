package com.epicodus.recommendations.api;

import com.epicodus.recommendations.model.ActiveListings;

import retrofit.Callback;

import retrofit.RequestInterceptor;
import retrofit.RestAdapter;

/**
 * Created by Guest on 11/10/15.
 */
public class Etsy {

    private static final String API_KEY = "hzmhs2u6cfnm9u8j0sij8xy8";

    private static RequestInterceptor getInterceptor() {
        return new RequestInterceptor() {
            @Override
            public void intercept(RequestFacade request) {
                request.addEncodedQueryParam("api_key", API_KEY);
            }
        };
    }

    private static Api getApi() {
        return new RestAdapter.Builder()
                .setEndpoint("https://openapi.etsy.com/v2")
                .setRequestInterceptor(getInterceptor())
                .build()
                .create(Api.class);
    }

    public static void getActiveListings(Callback<ActiveListings> callback) {
        getApi().activeListings("Images,Shop", callback);
    }
}
