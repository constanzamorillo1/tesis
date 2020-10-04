package uala.mvvm.core

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Product(
    @SerializedName("id") val id: String,
    @SerializedName("status") val status: String,
    @SerializedName("domain_id") val domainId: String,
    @SerializedName("name") val name: String
) : Serializable