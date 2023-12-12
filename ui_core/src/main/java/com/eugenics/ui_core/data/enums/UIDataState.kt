package com.eugenics.ui_core.data.enums

object UIDataState {
    const val LOADING = 10
    const val PREPARED = 11
    const val ERROR = 12

    fun getStateName(value: Int) = when (value) {
        10 -> "Loading"
        11 -> "Prepared"
        12 -> "Error"
        else -> "Nothing"
    }
}