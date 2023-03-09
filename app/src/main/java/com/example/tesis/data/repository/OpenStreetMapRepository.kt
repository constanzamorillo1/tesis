package com.example.tesis.data.repository

import com.example.tesis.data.manager.OpenStreetMapManager
import com.example.tesis.utils.Response
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

class OpenStreetMapRepository {

    private val openStreetMapManager = OpenStreetMapManager()

    fun getAddressPoint(address: String) = flow {
        emit(Response.Loading)
        val list = openStreetMapManager.getAddressPoint(address)
        emit(Response.Success(list.results[0].locations))
    }.catch { exception ->
        emit(Response.ErrorWithCode(exception.message))
    }

}

