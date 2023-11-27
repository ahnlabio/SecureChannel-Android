package io.myabcwallet.securechannel.network.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.myabcwallet.securechannel.BuildConfig
import kotlinx.serialization.json.Json
import okhttp3.Call
import okhttp3.Credentials
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.nio.charset.StandardCharsets
import javax.inject.Singleton

/**
 *
 * @author jin on 11/27/23
 */

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun providesNetworkJson(): Json = Json {
        ignoreUnknownKeys = true
    }

    @Provides
    @Singleton
    fun okHttpCallFactoryForAuth(): Call.Factory = OkHttpClient.Builder()
        .addInterceptor { chain ->
            val original = chain.request()
            val auth = Credentials.basic(
                username = BuildConfig.AUTH_ACCESS_KEY,
                password = BuildConfig.AUTH_ACCESS_SECRET,
                charset = StandardCharsets.UTF_8,
            )
            val request = original.newBuilder()
                .addHeader("Content-Type", "x-www-form-urlencoded")
                .addHeader("User-Agent", "ANDROID")
                .addHeader("Authorization", auth)
                .method(original.method, original.body)
                .build()

            return@addInterceptor chain.proceed(request)
        }
        .addInterceptor(
            HttpLoggingInterceptor()
                .apply {
                    if (BuildConfig.DEBUG) {
                        setLevel(HttpLoggingInterceptor.Level.BODY)
                    }
                },
        )
        .build()
}