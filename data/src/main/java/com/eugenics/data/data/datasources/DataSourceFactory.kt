package com.eugenics.data.data.datasources

import android.content.Context
import com.eugenics.data.interfaces.IFactory
import com.eugenics.data.interfaces.repository.IDataSource
import com.eugenics.data.interfaces.repository.ILocalDataSource


object DataSourceFactory : IFactory<IDataSource> {
    fun createRemoteDataSource(): IDataSource = NetworkIDataSourceImpl.newInstance()
    fun createFakeDataSource(): IDataSource = FakeIDataSourceImpl.newInstance()
    fun createLocalDataSource(context: Context): ILocalDataSource =
        LocalDataSourceImpl.newInstance(context = context)

    override fun create(): IDataSource {
        TODO("Not yet implemented")
    }
}