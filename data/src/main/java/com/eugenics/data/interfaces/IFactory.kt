package com.eugenics.data.interfaces

interface IFactory<out T> {
    fun create(): T
}