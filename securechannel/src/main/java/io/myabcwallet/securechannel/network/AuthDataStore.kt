package io.myabcwallet.securechannel.network

import io.myabcwallet.securechannel.model.data.SecureChannelResponse

/**
 *
 * @author jin on 11/23/23
 */
interface AuthDataStore {

    suspend fun createSecureChannel(
        publicKey: String,
        plainText: String,
    ) : SecureChannelResponse
}