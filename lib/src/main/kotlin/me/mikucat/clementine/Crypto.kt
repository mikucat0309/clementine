package me.mikucat.clementine

import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec
import kotlin.io.encoding.Base64

private val DEFAULT_KEY_SPEC = SecretKeySpec(Env.DEFAULT_KEY.encodeToByteArray(), "AES")
private val DEFAULT_IV_SPEC = IvParameterSpec(Env.DEFAULT_IV.encodeToByteArray())

internal fun encryptBody(plain: ByteArray): EncryptedBody {
    val encrypted = encrypt(plain, DEFAULT_KEY_SPEC, DEFAULT_IV_SPEC)
    val body = EncryptedBody(Base64.encode(encrypted))
    return body
}

internal fun encryptBodyWith(plain: ByteArray, key: String, iv: String): UserEncryptedBody {
    val keySpec = SecretKeySpec(key.encodeToByteArray(), "AES")
    val ivSpec = IvParameterSpec(iv.encodeToByteArray())
    val encrypted = encrypt(plain, keySpec, ivSpec)
    val body = UserEncryptedBody(Base64.encode(encrypted), key)
    return body
}

fun encrypt(plain: ByteArray, keySpec: SecretKeySpec, ivSpec: IvParameterSpec): ByteArray {
    val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
    cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec)
    val encrypted = cipher.doFinal(plain)
    return encrypted
}

fun decryptBFLoginToken(encrypted: String): BeanfunLoginToken? = runCatching {
    val encrypted = encrypted.hexToByteArray()
    val plain = decrypt(encrypted)
    val raw = plain.decodeToString()
    val token: BeanfunLoginToken = DefaultJSON.decodeFromString(raw)
    token
}.getOrNull()

fun decrypt(encrypted: ByteArray): ByteArray {
    val keySpec = DEFAULT_KEY_SPEC
    val ivSpec = DEFAULT_IV_SPEC
    val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
    cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec)
    val plain = cipher.doFinal(encrypted)
    return plain
}
