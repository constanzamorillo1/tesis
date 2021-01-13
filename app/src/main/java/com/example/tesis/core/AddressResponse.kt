package com.example.tesis.core

import com.google.gson.annotations.SerializedName

data class AddressResponse(
    @SerializedName("results") val results: List<Result>
)