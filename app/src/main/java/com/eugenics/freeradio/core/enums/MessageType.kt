package com.eugenics.freeradio.core.enums

enum class MessageType(private val code: Int) {
    WARNING(10),
    ERROR(11),
    INFO(13)
}