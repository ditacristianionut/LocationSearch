package com.dci.dev.locationsearch.di

import android.content.Context
import com.dci.dev.locationsearch.BuildConfig
import com.dci.dev.locationsearch.R
import com.dci.dev.locationsearch.data.api.LocationSearchApi
import com.dci.dev.locationsearch.data.api.positionstack.PositionStackLocationSearchApi
import com.dci.dev.locationsearch.data.repository.positionstack.PositionStackLocationSearchRepository
import com.dci.dev.locationsearch.domain.LocationSearchRepository
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

object Instances {

    private fun provideIODispatcher(): CoroutineDispatcher {
        return Dispatchers.IO
    }

    private fun provideMoshi(): Moshi {
        return Moshi.Builder()
            .addLast(KotlinJsonAdapterFactory())
            .build()
    }

    private fun provideMoshiConverterFactory(moshi: Moshi): MoshiConverterFactory {
        return MoshiConverterFactory
            .create(moshi)
    }

    private fun provideLoggingInterceptor(): HttpLoggingInterceptor {
        val interceptor = HttpLoggingInterceptor()
        if (BuildConfig.DEBUG) {
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
        } else {
            interceptor.setLevel(HttpLoggingInterceptor.Level.NONE)
        }
        return interceptor;
    }

    private fun provideOkHttpClient(loggingInterceptor: HttpLoggingInterceptor): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .build()
    }

    private fun provideLocationSearchApi(dataProvider: DataProvider, retrofit: Retrofit): LocationSearchApi {
        return when(dataProvider) {
            is DataProvider.PositionStackDataProvider -> retrofit.create(PositionStackLocationSearchApi::class.java)
        }
    }

    fun provideRetrofit(
        baseUrl: String,
        okHttpClient: OkHttpClient,
        moshiConverterFactory: MoshiConverterFactory
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(moshiConverterFactory)
            .client(okHttpClient)
            .build()
    }

    fun provideLocationSearchRepository(context: Context, provider: DataProvider): LocationSearchRepository {
        val ioDispatcher = provideIODispatcher()
        val loggingInterceptor = provideLoggingInterceptor()
        val okHttpClient = provideOkHttpClient(loggingInterceptor)
        val moshi = provideMoshi()
        val moshiConverterFactory = provideMoshiConverterFactory(moshi)
        val retrofit = provideRetrofit(provider.baseUrl, okHttpClient, moshiConverterFactory)
        return when(provider) {
            is DataProvider.PositionStackDataProvider -> PositionStackLocationSearchRepository(
                api = provideLocationSearchApi(provider, retrofit) as PositionStackLocationSearchApi,
                apiKey = provider.apiKey,
                dispatcher = ioDispatcher
            )
        }
    }

}

sealed class DataProvider(val apiKey: String, val baseUrl: String) {
    data class PositionStackDataProvider(private val context: Context): DataProvider(
        context.getString(
            R.string.position_stack_api_key),
        PositionStackLocationSearchApi.BASE_URL
    )
}