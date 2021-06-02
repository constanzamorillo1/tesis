package com.example.tesis.domain

import com.google.gson.annotations.SerializedName

data class Result(
    @SerializedName("locations") val locations: List<Location>
)
