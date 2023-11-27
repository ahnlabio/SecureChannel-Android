package io.myabcwallet.securechannel.model.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 *
 * @author jin on 11/23/23
 */

@Serializable
data class SecureChannelResponse(
    @SerialName("channelid") val channelId: String,
    @SerialName("publickey") val publicKey: String,
    val encrypted: String,
)