package uala.mvvm.domain

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

abstract class AbstractRepository<T> (service: Class<T>) {

    protected val service: T = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl(BASE_URL)
        .build()
        .create(service)

    companion object {
        private const val BASE_URL = "https://api.mercadolibre.com/"
    }
}

sealed class RepositoryResult<out T> {
    data class Success<out T>(val value: T) : RepositoryResult<T>()
    data class ErrorWithCode(val statusCode: Int?) : RepositoryResult<Nothing>()
}
