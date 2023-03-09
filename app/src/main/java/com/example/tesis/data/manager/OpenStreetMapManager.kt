package com.example.tesis.data.manager

import com.example.tesis.data.api.OpenStreetMapApi
import com.example.tesis.domain.AddressResponse

class OpenStreetMapManager: AbstractManager<OpenStreetMapApi>(OpenStreetMapApi::class.java) {

    suspend fun getAddressPoint(address: String): AddressResponse =
        service.getAddressPoint(KEY, address)

}