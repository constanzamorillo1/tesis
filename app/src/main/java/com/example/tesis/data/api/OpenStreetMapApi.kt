package com.example.tesis.data.api

import com.example.tesis.domain.AddressResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface OpenStreetMapApi {

    @GET("/geocoding/v1/address")
    suspend fun getAddressPoint(
        @Query("key") key: String,
        @Query("location") location: String
    ): AddressResponse

}