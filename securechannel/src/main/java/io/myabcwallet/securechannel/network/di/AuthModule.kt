package io.myabcwallet.securechannel.network.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.myabcwallet.securechannel.network.AuthDataStore
import io.myabcwallet.securechannel.network.retrofit.RetrofitAuthNetwork
import io.myabcwallet.securechannel.repository.AuthRepository
import io.myabcwallet.securechannel.repository.DefaultAuthRepository

/**
 *
 * @author jin on 11/27/23
 */
@Module
@InstallIn(SingletonComponent::class)
interface AuthModule {

    @Binds
    fun bindsAuthRepository(
        authRepository: DefaultAuthRepository
    ): AuthRepository

    @Binds
    fun bindsAuthDataSore(
        impl: RetrofitAuthNetwork
    ): AuthDataStore
}