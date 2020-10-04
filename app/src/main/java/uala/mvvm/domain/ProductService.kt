package uala.mvvm.domain

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query
import uala.mvvm.core.ProductResponse

interface ProductService {

    @GET("products/search")
    fun getProducts(
        @Query("status") status: String,
        @Query("site_id") site: String,
        @Query("q") q: String
    ) : Call<ProductResponse>
}