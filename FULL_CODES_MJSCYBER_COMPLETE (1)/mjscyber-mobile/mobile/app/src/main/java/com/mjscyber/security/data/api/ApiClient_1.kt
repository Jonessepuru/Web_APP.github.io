package com.mjscyber.security.data.api

import android.content.Context
import android.util.Log
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

/**
 * ApiClient – Retrofit + OkHttp with encrypted token storage
 * Task 2: Encrypt password/JWT, handle invalid inputs without crashing, logging for understanding
 */
object ApiClient {

    // TODO: Replace with your actual backend URL – for physical device, use your Afrihost/Render URL
    // For emulator: http://10.0.2.2:8001/api
    // For physical phone on same WiFi as backend: http://YOUR_LAPTOP_IP:8001/api
    // For production: https://api.mjscyber.co.za/api
    private const val BASE_URL = "https://api.mjscyber.co.za/api/" // Change to your backend

    private var retrofit: Retrofit? = null

    // EncryptedSharedPreferences – Task 2 encrypt password requirement
    private fun getEncryptedPrefs(context: Context) = try {
        val masterKey = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()
        EncryptedSharedPreferences.create(
            context,
            "mjscyber_secure_prefs",
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    } catch (e: Exception) {
        Log.e("ApiClient", "Encrypted prefs failed, using regular prefs: ${e.message}")
        context.getSharedPreferences("mjscyber_prefs", Context.MODE_PRIVATE)
    }

    fun saveToken(context: Context, token: String) {
        Log.d("ApiClient", "Saving token encrypted")
        getEncryptedPrefs(context).edit().putString("jwt_token", token).apply()
    }

    fun getToken(context: Context): String? {
        return getEncryptedPrefs(context).getString("jwt_token", null)
    }

    fun clearToken(context: Context) {
        Log.d("ApiClient", "Clearing token – logout")
        getEncryptedPrefs(context).edit().remove("jwt_token").apply()
    }

    fun saveUserRole(context: Context, role: String) {
        getEncryptedPrefs(context).edit().putString("user_role", role).apply()
    }

    fun getUserRole(context: Context): String? {
        return getEncryptedPrefs(context).getString("user_role", null)
    }

    fun getRetrofit(context: Context): Retrofit {
        if (retrofit != null) return retrofit!!

        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY // Logs for understanding – Task 2 requires logging
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .addInterceptor { chain ->
                // Add Authorization header from encrypted prefs
                val token = getToken(context)
                val request = if (token != null) {
                    chain.request().newBuilder()
                        .addHeader("Authorization", "Bearer $token")
                        .build()
                } else chain.request()
                chain.proceed(request)
            }
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()

        val moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()

        retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()

        Log.d("ApiClient", "Retrofit built with BASE_URL: $BASE_URL")
        return retrofit!!
    }

    fun getApi(context: Context): MjscyberApi {
        return getRetrofit(context).create(MjscyberApi::class.java)
    }
}
