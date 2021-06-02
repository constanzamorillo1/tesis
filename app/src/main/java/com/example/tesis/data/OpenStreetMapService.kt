package com.example.tesis.data

import com.example.tesis.domain.AddressResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface OpenStreetMapService {

    @GET("/geocoding/v1/address")
    fun getAddressPoint(
        @Query("key") key: String,
        @Query("location") location: String
    ): Call<AddressResponse>
}