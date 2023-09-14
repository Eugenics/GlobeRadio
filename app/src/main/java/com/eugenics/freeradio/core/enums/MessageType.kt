package com.eugenics.freeradio.core.enums

enum class MessageType(private val code: Int) {
    WARNING(10),
    ERROR(11),
    INFO(13),
    UI(14)
}

enum class InfoMessages {
    NO_DATA_TO_SAVE,
    NO_DATA_TO_LOAD
}