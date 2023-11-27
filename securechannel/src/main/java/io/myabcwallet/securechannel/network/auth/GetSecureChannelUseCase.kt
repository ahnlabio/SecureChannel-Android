package io.myabcwallet.securechannel.network.auth

import io.myabcwallet.securechannel.model.data.SecureChannelData
import io.myabcwallet.securechannel.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.bouncycastle.jce.ECNamedCurveTable
import org.bouncycastle.jce.interfaces.ECPrivateKey
import org.bouncycastle.jce.interfaces.ECPublicKey
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.bouncycastle.jce.spec.ECParameterSpec
import org.bouncycastle.jce.spec.ECPrivateKeySpec
import org.bouncycastle.jce.spec.ECPublicKeySpec
import org.bouncycastle.util.Arrays
import org.bouncycastle.util.encoders.Base64
import org.bouncycastle.util.encoders.Hex
import java.math.BigInteger
import java.security.KeyFactory
import java.security.KeyPairGenerator
import java.security.PrivateKey
import java.security.PublicKey
import java.security.SecureRandom
import java.security.Security
import java.security.spec.ECGenParameterSpec
import javax.crypto.Cipher
import javax.crypto.KeyAgreement
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec
import javax.inject.Inject

/**
 *
 * @author jin on 11/23/23
 */

class GetSecureChannelUseCase @Inject constructor(
    private val authRepository: AuthRepository,
) {

    operator fun invoke(): Flow<SecureChannelData?> {
        Security.removeProvider(BouncyCastleProvider.PROVIDER_NAME)
        Security.addProvider(BouncyCastleProvider())

        // 1. 키페어 생성
        val secp = ECGenParameterSpec("secp256r1")
        val generator = KeyPairGenerator.getInstance(
            "ECDH",
            BouncyCastleProvider.PROVIDER_NAME
        ).apply { initialize(secp, SecureRandom()) }
        val keyPair = generator.generateKeyPair()
        val privateKey = keyPair.private
        val privateKeyHexString = Hex.toHexString(privateKey.toBytes())
        val publicKey = keyPair.public
        val publicKeyHexString = Hex.toHexString(publicKey.toBytes())
        val plainText = System.currentTimeMillis().toString()

        return authRepository.createSecureChannel(
            publicKey = publicKeyHexString,
            plainText = plainText
        ).map { response ->
            // 2. 서버로 부터 받은 공개키 로드
            val publicKeyFromServerHexString = response.publicKey
            val publicKeyFromServer = loadPublicKey(Hex.decode(publicKeyFromServerHexString))
            // 3. 나의 개인키와 서버로부터 받은 공개키로 sharedSecret 생성
            val secret: ByteArray = getSharedSecretWithECDH(
                privateKey = privateKey,
                publicKey = publicKeyFromServer
            )
            // 4. AES CBC 로 평문 암호화
            val encryptedDataBase64EncodedString = plainText.encrypt(secret = secret)

            // 5. 서버로부터 받은 암호문과 내가 만든 암호문을 비교
            if (response.encrypted == encryptedDataBase64EncodedString) {
                SecureChannelData(
                    channelId = response.channelId,
                    secret = secret
                )
            } else {
                null
            }
        }
    }

    private fun PrivateKey.toBytes(): ByteArray =
        (this as ECPrivateKey).d.toByteArray()

    private fun PublicKey.toBytes(): ByteArray =
        (this as ECPublicKey).q.getEncoded(false)

    private fun loadPrivateKey(data: ByteArray): PrivateKey {
        val params: ECParameterSpec = ECNamedCurveTable.getParameterSpec("secp256r1")
        val privateKey = ECPrivateKeySpec(BigInteger(data), params)
        val keyFactory = KeyFactory.getInstance("ECDH", "BC")

        return keyFactory.generatePrivate(privateKey)
    }

    private fun loadPublicKey(data: ByteArray): PublicKey {
        val params: ECParameterSpec = ECNamedCurveTable.getParameterSpec("secp256r1")
        val publicKey = ECPublicKeySpec(params.curve.decodePoint(data), params)
        val keyFactory = KeyFactory.getInstance("ECDH", "BC")

        return keyFactory.generatePublic(publicKey)
    }

    private fun getSharedSecretWithECDH(
        privateKey: PrivateKey,
        publicKey: PublicKey
    ): ByteArray = getSharedSecretWithECDH(
        privateKey.toBytes(),
        publicKey.toBytes()
    )

    private fun getSharedSecretWithECDH(
        privateKeyData: ByteArray,
        publicKeyData: ByteArray
    ): ByteArray {
        val keyAgreement = KeyAgreement.getInstance(
            "ECDH",
            BouncyCastleProvider.PROVIDER_NAME
        )
        val privateKey: PrivateKey = loadPrivateKey(privateKeyData)
        val publicKey: PublicKey = loadPublicKey(publicKeyData)

        keyAgreement.init(privateKey)
        keyAgreement.doPhase(publicKey, true)

        return keyAgreement.generateSecret()
    }
}

fun String.encrypt(secret: ByteArray): String {
    val bytes = secret.split(16)
    val key = bytes[0]
    val iv = bytes[1]
    val keySpec = SecretKeySpec(key, "AES")
    val ivParameterSpec = IvParameterSpec(iv)

    return Cipher.getInstance(
        "AES/CBC/PKCS7Padding",
        BouncyCastleProvider.PROVIDER_NAME
    ).apply {
        init(Cipher.ENCRYPT_MODE, keySpec, ivParameterSpec)
    }.run {
        Base64.toBase64String(
            doFinal(this@encrypt.toByteArray(Charsets.UTF_8))
        )
    }
}

fun String.decrypt(secret: ByteArray): String {
    val bytes = secret.split(16)
    val key = bytes[0]
    val iv = bytes[1]
    val keySpec = SecretKeySpec(key, "AES")
    val ivParameterSpec = IvParameterSpec(iv)

    return Cipher.getInstance(
        "AES/CBC/PKCS7Padding",
        BouncyCastleProvider.PROVIDER_NAME
    ).apply {
        init(Cipher.DECRYPT_MODE, keySpec, ivParameterSpec)
    }.run {
        Base64.toBase64String(
            doFinal(this@decrypt.toByteArray(Charsets.UTF_8))
        )
    }
}

@Throws(ArrayIndexOutOfBoundsException::class)
fun ByteArray.split(index: Int): ArrayList<ByteArray> {
    if (index > this.size) {
        throw ArrayIndexOutOfBoundsException()
    }

    return arrayListOf<ByteArray>().apply {
        val input = this@split

        add(Arrays.copyOfRange(input, 0, index))
        add(Arrays.copyOfRange(input, index, input.size))
    }
}
