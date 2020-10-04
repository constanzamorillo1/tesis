package uala.mvvm.domain

import android.util.Log
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import uala.mvvm.core.ProductResponse

class ProductRepository : AbstractRepository<ProductService>(ProductService::class.java) {

    fun getProducts(q: String, block: (RepositoryResult<ProductResponse>) -> Unit) {
        service.getProducts(STATUS, SITE_ID, q).enqueue(getCallBack(block))
    }

    private fun getCallBack(block: (RepositoryResult<ProductResponse>) -> Unit) = object : Callback<ProductResponse> {
        override fun onFailure(call: Call<ProductResponse>, t: Throwable) {
            Log.d("onFailure", "fallo la carga de datos")
        }

        override fun onResponse(call: Call<ProductResponse>, response: Response<ProductResponse>) {
            if (response.isSuccessful) {
                response.body()?.let {
                    block(RepositoryResult.Success(it))
                }
            } else {
                block(RepositoryResult.ErrorWithCode(response.code()))
            }
        }
    }

    companion object {
        private const val STATUS = "active"
        private const val SITE_ID = "MLM"
    }
}