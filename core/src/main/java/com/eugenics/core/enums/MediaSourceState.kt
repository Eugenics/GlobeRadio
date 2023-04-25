package com.eugenics.core.enums

enum class MediaSourceState(val value: Int) {
    STATE_IDL(0),
    STATE_CREATED(1),
    STATE_INITIALIZING(2),
    STATE_INITIALIZED(3),
    STATE_ERROR(4),
    STATE_ON_CLICK(5);

    companion object {
        fun getNameByValue(value: Int): String {
            for (state in MediaSourceState.values()) {
                if (state.value == value)
                    return state.name
            }
            return ""
        }
    }
}