package com.eugenics.core.interfaces

import android.content.ComponentName

interface IFactory<out T> {
    fun create(componentName: ComponentName): T
}