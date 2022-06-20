package com.eugenics.freeradio.util

import com.eugenics.freeradio.util.error.Failure

sealed class Respond<out T> {
    data class Success<out T>(val data: T) : Respond<T>()
    data class Error(val failure: Failure) : Respond<Nothing>()
    object Loading : Respond<Nothing>()
}