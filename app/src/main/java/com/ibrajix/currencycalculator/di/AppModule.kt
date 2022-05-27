package com.ibrajix.currencycalculator.di

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.ibrajix.currencycalculator.BuildConfig
import com.ibrajix.currencycalculator.network.ApiDataSource
import com.ibrajix.currencycalculator.network.ApiService
import com.ibrajix.currencycalculator.utils.EndPoints
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    //Api Base Url
    @Provides
    fun providesBaseUrl() = EndPoints.BASE_URL

    //Gson for converting JSON String to Java Objects
    @Provides
    fun providesGson() : Gson = GsonBuilder().setLenient().create()

    //Retrofit for networking
    @OptIn(ExperimentalSerializationApi::class)
    @Provides
    @Singleton
    fun provideRetrofit(gson: Gson) : Retrofit{
        val contentType = "application/json".toMediaType()
        val converterFactory = Json.asConverterFactory(contentType)
        return Retrofit.Builder()
            .baseUrl(EndPoints.BASE_URL)
            .client(
                OkHttpClient.Builder().also { client ->
                    if (BuildConfig.DEBUG){
                        val logging = HttpLoggingInterceptor()
                        logging.setLevel(HttpLoggingInterceptor.Level.BODY)
                        client.addInterceptor(logging)
                        client.connectTimeout(120, TimeUnit.SECONDS)
                        client.readTimeout(120, TimeUnit.SECONDS)
                        client.protocols(Collections.singletonList(Protocol.HTTP_1_1))
                    }
                }.build())
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }


    //api Service with retrofit instance
    @Provides
    @Singleton
    fun provideApiService(retrofit: Retrofit) : ApiService = retrofit.create(ApiService::class.java)

    @Provides
    @Singleton
    fun provideApiDataSource(apiService: ApiService) = ApiDataSource(apiService)

}