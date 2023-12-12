package com.eugenics.ui_core.data.model

import android.content.Context
import com.eugenics.resource.R
import java.util.UUID

data class UIMessage(
    val id: String,
    val messageType: Int,
    val messageInfoType: Int,
    val messageText: String
) {
    fun getMessage(context: Context): String =
        this.messageText.ifEmpty {
            when (this.messageInfoType) {
                INFO_NO_DATA_TO_SAVE -> context.getString(R.string.no_data_save)
                INFO_NO_DATA_TO_LOAD -> context.getString(R.string.no_data_load)
                INFO_FIRST_INIT -> context.getString(R.string.init_load_text)
                INFO_LOADING -> context.getString(R.string.loading_string)
                INFO_ERROR -> context.getString(R.string.unexpected_error)
                else -> context.getString(R.string.empty_message)
            }
        }


    companion object {
        const val TYPE_WARNING = 10
        const val TYPE_ERROR = 11
        const val TYPE_INFO = 13
        const val TYPE_UI = 14

        const val INFO_NO_DATA_TO_SAVE = 100
        const val INFO_NO_DATA_TO_LOAD = 200
        const val INFO_FIRST_INIT = 300
        const val INFO_LOADING = 400
        const val INFO_ERROR = 500
        const val INFO_INFO = 600

        fun emptyInstance() =
            UIMessage(
                id = UUID.randomUUID().toString(),
                messageType = TYPE_INFO,
                messageInfoType = INFO_LOADING,
                messageText = ""
            )

        fun newInstance(
            id: String,
            messageType: Int,
            messageInfoType: Int,
            messageText: String
        ): UIMessage =
            UIMessage(
                id = id,
                messageType = messageType,
                messageInfoType = messageInfoType,
                messageText = messageText
            )

    }
}