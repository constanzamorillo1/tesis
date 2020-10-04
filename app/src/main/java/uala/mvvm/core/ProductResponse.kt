package uala.mvvm.core

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class ProductResponse(
    @SerializedName("results") val results: List<Product>
) : Serializable