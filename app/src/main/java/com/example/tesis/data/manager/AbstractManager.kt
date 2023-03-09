package com.example.tesis.data.manager

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

abstract class AbstractManager<T> (service: Class<T>) {

    protected val service: T = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl(BASE_URL)
        .build()
        .create(service)

    companion object {
        private const val BASE_URL = "http://www.mapquestapi.com/"
        const val KEY = "A3qGuyvHi1WfLxj1KKh51zxDspxAfOAq"
    }
}