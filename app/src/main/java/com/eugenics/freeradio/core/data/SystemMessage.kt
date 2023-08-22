package com.eugenics.freeradio.core.data

import com.eugenics.freeradio.core.enums.MessageType
import java.util.UUID

data class SystemMessage(
    val id: String,
    val type: MessageType,
    val message: String
) {
    companion object {
        fun emptyInstance() =
            SystemMessage(
                id = UUID.randomUUID().toString(),
                type = MessageType.INFO,
                message = ""
            )

        fun newInstance(id: String, type: MessageType, message: String): SystemMessage =
            SystemMessage(
                id = id,
                type = type,
                message = message
            )

    }
}
