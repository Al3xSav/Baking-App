/*
Copyright 2018 Savtsouk Alexios

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */

package com.alexsav.baking_app.api;

import android.support.annotation.NonNull;

import com.alexsav.baking_app.BuildConfig;
import com.alexsav.baking_app.models.Recipes;
import com.orhanobut.logger.Logger;

import java.io.Serializable;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

public final class NetworkUtilsAPI implements Serializable {

    private static volatile NetworkUtilsAPI networkUtilsAPI = new NetworkUtilsAPI();
    private NetworkServiceAPI networkServiceAPI;

    private NetworkUtilsAPI() {
        //Prevent from the reflection api.
        if (networkUtilsAPI != null) {
            throw new RuntimeException("Get single instance of this class with getInstance().");
        }

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BuildConfig.API_KEY)
                .addConverterFactory(JacksonConverterFactory.create())
                .build();

        networkServiceAPI = retrofit.create(NetworkServiceAPI.class);
    }

    public static NetworkUtilsAPI getInstance() {
        if (networkUtilsAPI == null) {
            synchronized (NetworkUtilsAPI.class) {
                if (networkUtilsAPI == null) {
                    networkUtilsAPI = new NetworkUtilsAPI();
                }
            }
        }
        return networkUtilsAPI;
    }

    public void getRecipes(final NetworkCallBackAPI<List<Recipes>> networkCallBackAPI) {
        networkServiceAPI.getRecipes().enqueue(new Callback<List<Recipes>>() {
            @Override
            public void onResponse(@NonNull Call<List<Recipes>> call, @NonNull Response<List<Recipes>> response) {
                networkCallBackAPI.onResponse(response.body());
            }

            @Override
            public void onFailure(@NonNull Call<List<Recipes>> call, @NonNull Throwable throwable) {
                if (call.isCanceled()) {
                    Logger.e("Request cancelled");
                    networkCallBackAPI.onCancel();
                } else {
                    Logger.e(throwable.getMessage());
                    networkCallBackAPI.onResponse(null);
                }
            }
        });
    }

}

