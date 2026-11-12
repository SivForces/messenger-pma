package com.messenger.mobile.data

import android.content.Context
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import com.messenger.mobile.MessengerApp
import okhttp3.OkHttpClient.Builder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiConfig {
    const val BASE_URL = "http://10.0.2.2:8080/"
}

class AppContainer(context: Context) {
    val tokenStore = TokenStore(context)

    val settingsStore = SettingsStore(context)

    @Volatile
    var sessionToken: String? = null

    private val client = Builder()
        .addInterceptor { chain ->
            val token = sessionToken
            val request = if (token != null)
                chain.request().newBuilder().header("Authorization", "Bearer $token").build()
            else chain.request()
            chain.proceed(request)
        }
        .build()

    val api: ApiService = Retrofit.Builder()
        .baseUrl(ApiConfig.BASE_URL)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(ApiService::class.java)
}

fun CreationExtras.container(): AppContainer =
    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as MessengerApp).container
