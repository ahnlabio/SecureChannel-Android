package io.myabcwallet.securechannel.repository

import io.myabcwallet.securechannel.model.data.SecureChannelResponse
import kotlinx.coroutines.flow.Flow

/**
 *
 * @author jin on 11/23/23
 */
interface AuthRepository {

    fun createSecureChannel(
        publicKey: String,
        plainText: String,
        environment: String,
    ): Flow<SecureChannelResponse>
}