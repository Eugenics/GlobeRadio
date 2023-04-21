package com.eugenics.core.enums

enum class MediaSourceState(val value: Int) {
    STATE_IDL(0),
    STATE_CREATED(1),
    STATE_INITIALIZING(2),
    STATE_INITIALIZED(3),
    STATE_ERROR(4)
}