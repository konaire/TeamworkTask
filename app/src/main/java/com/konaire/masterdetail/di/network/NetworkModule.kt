package com.konaire.masterdetail.di.network

import android.util.Base64

import com.google.gson.GsonBuilder

import com.konaire.masterdetail.BuildConfig
import com.konaire.masterdetail.network.Api
import com.konaire.masterdetail.util.Constants

import dagger.Module
import dagger.Provides

import io.reactivex.schedulers.Schedulers

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.logging.HttpLoggingInterceptor.Level

import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

import java.util.concurrent.TimeUnit

import javax.inject.Singleton

/**
 * Created by Evgeny Eliseyev on 23/04/2018.
 */
@Module
class NetworkModule {
    @Singleton
    @Provides
    fun provideOkHttpClient(): OkHttpClient {
        val builder = OkHttpClient.Builder()
        builder.connectTimeout(30, TimeUnit.SECONDS)
        builder.readTimeout(30, TimeUnit.SECONDS)

        val loggingLevel = if (BuildConfig.DEBUG) Level.BODY else Level.NONE
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.level = loggingLevel
        builder.addInterceptor { chain ->
            val request = chain.request().newBuilder()
            val userpassword = Base64.encodeToString("${Constants.API_TOKEN}:xxx".toByteArray(), Base64.DEFAULT).trim()
            request.addHeader("Authorization", "BASIC $userpassword")
            chain.proceed(request.build())
        }

        builder.addInterceptor(loggingInterceptor)

        return builder.build()
    }

    @Singleton
    @Provides
    fun provideRetrofit(
        client: OkHttpClient
    ): Retrofit {
        val builder = Retrofit.Builder().client(client)
        builder.addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
        builder.addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io()))
        builder.baseUrl(Constants.BASE_URL)

        return builder.build()
    }

    @Provides
    fun provideApi(
        retrofit: Retrofit
    ): Api {
        return retrofit.create(Api::class.java)
    }
}