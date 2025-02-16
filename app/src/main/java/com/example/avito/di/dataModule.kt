package com.example.avito.di

import com.example.avito.data.api.DeezerApi
import com.example.avito.repository.DeezerRepository
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


// Модуль для внедрения зависимостей
val dataModule = module {
    single { provideRetrofit() }
    single { provideDeezerApi(get()) }
    single { DeezerRepository(get()) }
}

// Функция для создания Retrofit-клиента
fun provideRetrofit(): Retrofit {
    return Retrofit.Builder()
        .baseUrl("https://api.deezer.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .client(
            OkHttpClient.Builder()
                .addInterceptor(HttpLoggingInterceptor().apply {
                    level = HttpLoggingInterceptor.Level.BODY
                })
                .build()
        )
        .build()
}

// Функция для создания API-интерфейса
fun provideDeezerApi(retrofit: Retrofit): DeezerApi {
    return retrofit.create(DeezerApi::class.java)
}