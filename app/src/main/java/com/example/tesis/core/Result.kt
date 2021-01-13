package com.example.tesis.core

import com.google.gson.annotations.SerializedName

data class Result(
    @SerializedName("locations") val locations: List<Location>
)
