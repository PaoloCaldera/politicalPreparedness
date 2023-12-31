package com.example.android.politicalpreparedness.network

import okhttp3.OkHttpClient

class CivicsHttpClient : OkHttpClient() {

    companion object {
        private const val API_KEY = "AIzaSyAaGCk9urUH-3K_fJJ2VggvRDYrbmSnGis"

        fun getClient(): OkHttpClient {
            return Builder()
                .addInterceptor { chain ->
                    val original = chain.request()
                    val url = original
                        .url()
                        .newBuilder()
                        .addQueryParameter("key", API_KEY)
                        .build()
                    val request = original
                        .newBuilder()
                        .url(url)
                        .build()
                    chain.proceed(request)
                }
                .build()
        }
    }

}