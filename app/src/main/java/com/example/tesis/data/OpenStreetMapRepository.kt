package com.example.tesis.data

import com.example.tesis.domain.AddressResponse
import com.example.tesis.domain.AbstractRepository
import com.example.tesis.domain.RepositoryResult
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class OpenStreetMapRepository:
    AbstractRepository<OpenStreetMapService>(OpenStreetMapService::class.java) {

    fun getAddressPoint(address: String, block: (RepositoryResult<AddressResponse>) -> Unit) {
        service.getAddressPoint(KEY, address).enqueue(getCallBack(block))
    }

    private fun getCallBack(block: (RepositoryResult<AddressResponse>) -> Unit) = object: Callback<AddressResponse> {
        override fun onFailure(call: Call<AddressResponse>, t: Throwable) {
            block(RepositoryResult.ErrorWithCode(t.hashCode()))
        }

        override fun onResponse(call: Call<AddressResponse>, response: Response<AddressResponse>) {
            if (response.isSuccessful) {
                response.body()?.let {
                    block(RepositoryResult.Success(it))
                }
            } else {
                block(RepositoryResult.ErrorWithCode(response.code()))
            }
        }

    }
}

