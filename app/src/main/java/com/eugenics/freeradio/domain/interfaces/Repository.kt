package com.eugenics.freeradio.domain.interfaces

import com.eugenics.freeradio.domain.model.Tag

interface Repository {
    fun getTags(): List<Tag>
}