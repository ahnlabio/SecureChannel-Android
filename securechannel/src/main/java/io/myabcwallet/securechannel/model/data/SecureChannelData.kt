package io.myabcwallet.securechannel.model.data

/**
 *
 * @author jin on 11/24/23
 */
data class SecureChannelData(
    val channelId: String,
    val secret: ByteArray,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as SecureChannelData

        if (channelId != other.channelId) return false
        if (!secret.contentEquals(other.secret)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = channelId.hashCode()
        result = 31 * result + secret.contentHashCode()
        return result
    }
}
