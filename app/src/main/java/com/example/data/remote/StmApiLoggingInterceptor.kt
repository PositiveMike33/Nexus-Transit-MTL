package com.example.data.remote

import android.util.Log
import com.example.BuildConfig
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

/**
 * Intercepteur OkHttp dédié au monitoring de la télémétrie GTFS-RT STM.
 * Inspecte et enregistre le trafic HTTP en temps réel (URL, latence, statut HTTP et taille du payload),
 * et injecte automatiquement l'en-tête apiKey depuis BuildConfig si configuré.
 */
class StmApiLoggingInterceptor : Interceptor {

    companion object {
        private const val TAG = "STM_GTFS_RT_API"
    }

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val startTime = System.currentTimeMillis()

        // Injection automatique de la clé d'API STM depuis BuildConfig si non présente dans l'en-tête
        val requestBuilder = originalRequest.newBuilder()
        val existingApiKey = originalRequest.header("apiKey")
        if (existingApiKey.isNullOrEmpty() && BuildConfig.STM_CLIENT_ID.isNotBlank() && !BuildConfig.STM_CLIENT_ID.contains("MY_STM_CLIENT_ID")) {
            requestBuilder.header("apiKey", BuildConfig.STM_CLIENT_ID)
        }

        val request = requestBuilder.build()

        Log.d(TAG, "--> [REQ STM GTFS-RT] ${request.method} ${request.url}")

        val response: Response = try {
            chain.proceed(request)
        } catch (e: Exception) {
            Log.e(TAG, "<-- [ERR STM GTFS-RT] Échec réseau vers STM API: ${e.localizedMessage}")
            throw e
        }

        val durationMs = System.currentTimeMillis() - startTime
        val responseBody = response.body
        val contentLength = responseBody?.contentLength() ?: -1L
        val sizeFormatted = if (contentLength >= 0) "$contentLength octets" else "flux streaming/inconnu"

        if (response.isSuccessful) {
            Log.i(
                TAG,
                "<-- [RES ${response.code}] ${request.url.encodedPath} ($durationMs ms, $sizeFormatted)"
            )
        } else {
            Log.w(
                TAG,
                "<-- [WARN ${response.code}] ${request.url.encodedPath} ($durationMs ms) : ${response.message}"
            )
        }

        return response
    }
}
