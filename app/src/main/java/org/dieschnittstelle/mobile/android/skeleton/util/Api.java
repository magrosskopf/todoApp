package org.dieschnittstelle.mobile.android.skeleton.util;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Api {

    public static ApiInterface getClient() {
        OkHttpClient client = new OkHttpClient.Builder().build();
        Retrofit adapter = new Retrofit.Builder()
                .baseUrl("http://10.0.2.2:8080/") //Setting the Root URL
                .addConverterFactory(GsonConverterFactory.create())
                .build(); //Finally building the adapter

        //Creating object for our interface
        ApiInterface api = adapter.create(ApiInterface.class);
        return api;
    }

}
