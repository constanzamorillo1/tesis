package com.example.tesis.domain

import com.google.gson.annotations.SerializedName

data class AddressResponse(
    @SerializedName("results") val results: List<Result>
)