package com.eugenics.media_service.domain.interfaces

interface IFactory<out T> {
    fun create(): T
}