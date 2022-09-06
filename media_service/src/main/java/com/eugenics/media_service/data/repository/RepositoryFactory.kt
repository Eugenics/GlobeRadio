package com.eugenics.media_service.data.repository

import android.content.Context
import com.eugenics.media_service.domain.interfaces.repository.IRepository

object RepositoryFactory {
    fun create(context: Context): IRepository = IRepositoryImpl.newInstance(context = context)
}