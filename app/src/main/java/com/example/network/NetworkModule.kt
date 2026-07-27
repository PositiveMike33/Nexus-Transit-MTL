package com.example.network

import com.example.data.remote.StmApiLoggingInterceptor
import com.example.data.remote.StmApiService
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Module réseau fournissant les configurations OkHttpClient, Moshi et Retrofit
 * pour monitorer et interagir avec l'API GTFS-RT de la STM.
 */
object NetworkModule {

    private const val BASE_URL = "https://api.stm.info/"

    /**
     * Intercepteur de journalisation standard pour le corps des requêtes/réponses HTTP.
     */
    private val httpLoggingInterceptor: HttpLoggingInterceptor by lazy {
        HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }

    /**
     * Intercepteur personnalisé pour la télémétrie GTFS-RT de la STM.
     */
    val stmApiLoggingInterceptor: StmApiLoggingInterceptor by lazy {
        StmApiLoggingInterceptor()
    }

    /**
     * Instance OkHttpClient configurée avec les intercepteurs de journalisation et les timeouts appropriés.
     */
    val okHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .writeTimeout(15, TimeUnit.SECONDS)
            .addInterceptor(stmApiLoggingInterceptor)
            .addInterceptor(httpLoggingInterceptor)
            .build()
    }

    /**
     * Instance Moshi pour la sérialisation / désérialisation JSON GTFS-RT.
     */
    val moshi: Moshi by lazy {
        Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
    }

    /**
     * Instance Retrofit configurée pour l'API STM.
     */
    val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
    }

    /**
     * Service Retrofit prêt à l'emploi pour consommer les flux GTFS-RT de la STM.
     */
    val stmApiService: StmApiService by lazy {
        retrofit.create(StmApiService::class.java)
    }

    /**
     * Permet de créer un service Retrofit personnalisé avec une URL de base spécifique (ex: tests ou environnement de démo).
     */
    fun createStmApiService(baseUrl: String = BASE_URL): StmApiService {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(StmApiService::class.java)
    }
}
