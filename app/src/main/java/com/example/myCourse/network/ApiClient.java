package com.example.myCourse.network;

import com.example.myCourse.utils.LocalDateAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.time.LocalDate;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    // This never changes—your Plesk‐hosted Node app’s root URL:
    private static final String BASE_URL = "https://api.mycoursec868.com/";

    private static Retrofit retrofit;

    public static Retrofit getClient() {
        if (retrofit == null) {
            // 1) build a Gson instance with your LocalDate adapter
            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
                    .create();

            // 2) build an OkHttpClient that injects the x-internal-secret header
            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(new Interceptor() {
                        @Override
                        public Response intercept(Chain chain) throws IOException {
                            Request original = chain.request();
                            Request withSecret = original.newBuilder()
                                    .header("x-internal-secret", com.example.myCourse.BuildConfig.X_INTERNAL_SECRET)
                                    .method(original.method(), original.body())
                                    .build();
                            return chain.proceed(withSecret);
                        }
                    })
                    .build();

            // 3) build Retrofit using that client and your gson converter
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();
        }
        return retrofit;
    }
}
