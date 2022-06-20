package com.eugenics.freeradio.di

import android.app.Application
import android.util.Log
import androidx.room.Room
import com.eugenics.freeradio.data.constants.BASE_URL
import com.eugenics.freeradio.data.database.DataBase
import com.eugenics.freeradio.data.network.ApiService
import com.eugenics.freeradio.data.repository.FakeDataSourceImpl
import com.eugenics.freeradio.data.repository.NetworkDataSourceImpl
import com.eugenics.freeradio.data.repository.RepositoryImpl
import com.eugenics.freeradio.domain.core.Player
import com.eugenics.freeradio.domain.repository.DataSource
import com.eugenics.freeradio.domain.repository.ILocalDataSource
import com.eugenics.freeradio.domain.repository.Repository
import com.eugenics.freeradio.domain.usecases.*
import com.eugenics.freeradio.player.PlayerImpl
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    private val gson: Gson = GsonBuilder()
        .setPrettyPrinting()
        .create()

    private val interceptor: HttpLoggingInterceptor = HttpLoggingInterceptor(
        logger = { message ->
            Log.e("http interceptor log", message)
        })
        .setLevel(HttpLoggingInterceptor.Level.BODY)

    private val contenetType = "application/json"

    @Provides
    @Singleton
    fun provideHttpClient(): OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(interceptor)
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .build()

    @Provides
    @Singleton
    fun provideGsonFactory(): GsonConverterFactory = GsonConverterFactory.create(gson)

    @Provides
    @Singleton
    fun provideNetworkApiService(
        okHttpClient: OkHttpClient,
        gsonConverterFactory: GsonConverterFactory
    ): ApiService = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(gsonConverterFactory)
//        .addCallAdapterFactory(CoroutineCallAdapterFactory())
        .client(okHttpClient)
        .build()
        .create(ApiService::class.java)

    @Provides
    @Singleton
    @Named("NetworkDataSource")
    fun provideNetworkDataSource(apiService: ApiService): DataSource =
        NetworkDataSourceImpl(apiService = apiService)

    @Provides
    @Singleton
    @Named("FakeDataSource")
    fun provideFakeDataSource(): DataSource =
        FakeDataSourceImpl()

    @Provides
    @Singleton
    fun provideRepository(
        @Named("NetworkDataSource") dataSource: DataSource,
        localDataSource: ILocalDataSource
    ): Repository =
        RepositoryImpl(
            dataSource = dataSource,
            localDataSource = localDataSource
        )

    @Provides
    @Singleton
    fun provideUseCase(repository: Repository) = UseCase(
        getStationsByNameUseCase = GetStationsByNameUseCase(repository = repository),
        getStationsByTagUseCase = GetStationsByTagUseCase(repository = repository),
        getStationsUseCase = GetStationsUseCase(repository = repository),
        getStationsLocalUseCase = GetStationsLocalUseCase(repository = repository),
        getStationsByNameLocalUseCase = GetStationsByNameLocalUseCase(repository = repository),
        getStationsByTagLocalUseCase = GetStationsByTagLocalUseCase(repository = repository),
        insertStationIntoDbUseCase = InsertStationIntoDbUseCase(repository = repository)
    )

    @Provides
    @Singleton
    fun providePlayer(application: Application): Player =
        PlayerImpl.initialize(context = application.applicationContext)

    @Provides
    @Singleton
    fun provideDataBase(application: Application): DataBase =
        Room.databaseBuilder(
            application,
            DataBase::class.java,
            "local_db.db"
        ).build()
}