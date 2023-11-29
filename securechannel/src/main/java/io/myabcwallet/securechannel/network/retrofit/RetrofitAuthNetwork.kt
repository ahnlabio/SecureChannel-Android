package io.myabcwallet.securechannel.network.retrofit

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import io.myabcwallet.securechannel.BuildConfig
import io.myabcwallet.securechannel.model.data.SecureChannelResponse
import io.myabcwallet.securechannel.network.AuthDataStore
import kotlinx.serialization.json.Json
import okhttp3.Call
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.Header
import retrofit2.http.POST
import javax.inject.Inject

/**
 *
 * @author jin on 2023/06/30
 */

private interface AuthApi {

    @FormUrlEncoded
    @POST("secure/channel/create")
    suspend fun createSecureChannel(
        @Field("pubkey") publicKey: String,
        @Field("plain") plainText: String,
    ): SecureChannelResponse
}

private const val AUTH_BASE_URL = BuildConfig.SERVER_AUTH_URL
private const val DEV_AUTH_BASE_URL = BuildConfig.DEV_SERVER_AUTH_URL

internal class RetrofitAuthNetwork @Inject constructor(
    networkJson: Json,
    okhttpCallFactory: Call.Factory,
) : AuthDataStore {

    private val authApi = Retrofit.Builder()
        .baseUrl(AUTH_BASE_URL)
        .callFactory(okhttpCallFactory)
        .addConverterFactory(
            networkJson.asConverterFactory("application/json".toMediaType())
        )
        .build()
        .create(AuthApi::class.java)

    private val devAuthApi = Retrofit.Builder()
        .baseUrl(DEV_AUTH_BASE_URL)
        .callFactory(okhttpCallFactory)
        .addConverterFactory(
            networkJson.asConverterFactory("application/json".toMediaType())
        )
        .build()
        .create(AuthApi::class.java)

    override suspend fun createSecureChannel(
        publicKey: String,
        plainText: String,
        isDev: Boolean,
    ): SecureChannelResponse =
        (if (isDev) devAuthApi else authApi)
            .createSecureChannel(
                publicKey = publicKey,
                plainText = plainText,
            )
}