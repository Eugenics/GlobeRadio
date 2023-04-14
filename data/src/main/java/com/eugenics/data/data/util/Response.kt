package com.eugenics.data.data.util

private const val LOADING_MESSAGE = "Loading..."
private const val SUCCESS_MESSAGE = "Success..."
private const val EMPTY_STRING = ""

sealed class Response<T>(
    val data: T? = null,
    val message: String = EMPTY_STRING
) {
    class Success<T>(data: T) : Response<T>(data = data, message = SUCCESS_MESSAGE)
    class Error<T>(error: String) : Response<T>(message = error)
    class Loading<T> : Response<T>(message = LOADING_MESSAGE)
}