package com.eugenics.ui_core.data.enums

sealed class DataState(val code: Int, val name: String) {
    data object Loading : DataState(code = 10, name = "Loading")
    data object Prepared : DataState(code = 11, name = "Prepared")
    data object Error : DataState(code = 12, name = "Error")
}
