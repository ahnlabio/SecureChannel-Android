package io.myabcwallet.securechannel.repository

import io.myabcwallet.securechannel.model.data.SecureChannelResponse
import io.myabcwallet.securechannel.network.AuthDataStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

/**
 *
 * @author jin on 11/23/23
 */
internal class DefaultAuthRepository @Inject constructor(
    private val authDataStore: AuthDataStore
) : AuthRepository {

    override fun createSecureChannel(
        publicKey: String,
        plainText: String,
        environment: String,
    ): Flow<SecureChannelResponse> = flow {
        emit(
            authDataStore.createSecureChannel(
                publicKey = publicKey,
                plainText = plainText,
                environment = environment,
            )
        )
    }.flowOn(Dispatchers.IO)
}