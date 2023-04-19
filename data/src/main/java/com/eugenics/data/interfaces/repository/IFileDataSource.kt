package com.eugenics.data.interfaces.repository

import com.eugenics.core.model.Tag
import com.eugenics.core.interfaces.IDataSource

interface IFileDataSource : IDataSource {
    fun getTags(): List<Tag>
}