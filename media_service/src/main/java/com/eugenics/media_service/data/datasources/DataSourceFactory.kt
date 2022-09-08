package com.eugenics.media_service.data.datasources

import android.content.Context
import com.eugenics.media_service.domain.interfaces.IFactory
import com.eugenics.media_service.domain.interfaces.repository.IDataSource
import com.eugenics.media_service.domain.interfaces.repository.ILocalDataSource

object DataSourceFactory : IFactory<IDataSource> {
    fun createRemoteDataSource(): IDataSource = NetworkIDataSourceImpl.newInstance()
    fun createFakeDataSource(): IDataSource = FakeIDataSourceImpl.newInstance()
    fun createLocalDataSource(context: Context): ILocalDataSource =
        LocalDataSourceImpl.newInstance(context = context)

    override fun create(): IDataSource {
        TODO("Not yet implemented")
    }
}