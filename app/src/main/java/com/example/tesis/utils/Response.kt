package com.example.tesis.utils

sealed class Response<out T> {

    object Loading: Response<Nothing>()
    data class Success<out T>(val value: T) : Response<T>()
    data class ErrorWithCode(val message: String?) : Response<Nothing>()

}