package me.mikucat.clementine.app.repo

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import kotlinx.coroutines.flow.first
import me.mikucat.clementine.app.model.GCMParams
import java.security.Key
import java.security.KeyStore
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.spec.GCMParameterSpec


class KeyStoreRepo(
    private val appData: AppDataRepo,
) {
    private val keyStore by lazy {
        KeyStore.getInstance(KEYSTORE_PROVIDER).apply {
            load(null)
        }
    }

    private fun genKey(): Key {
        val generator = KeyGenerator.getInstance(KEY_ALGORITHM, KEYSTORE_PROVIDER)
        val spec = KeyGenParameterSpec
            .Builder(
                KEY_ALIAS,
                KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT,
            )
            .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
            .build()
        generator.init(spec)
        val secretKey = generator.generateKey()!!
        return secretKey
    }

    private fun getOrGenKey(): Key {
        return if (keyStore.containsAlias(KEY_ALIAS))
            keyStore.getKey(KEY_ALIAS, null)!!
        else
            genKey()
    }

    suspend fun encrypt(data: ByteArray): ByteArray {
        val cipher = Cipher.getInstance(KEY_TRANSFORMATION)
        val key = getOrGenKey()
        cipher.init(Cipher.ENCRYPT_MODE, key)
        val iv = cipher.iv
        val aad = SecureRandom().generateSeed(AAD_LENGTH)
        cipher.updateAAD(aad)
        val params = GCMParams(iv, aad)
        appData.update(params)
        return cipher.doFinal(data)!!
    }

    suspend fun decrypt(data: ByteArray): ByteArray {
        val cipher = Cipher.getInstance(KEY_TRANSFORMATION)
        val key = getOrGenKey()
        val params = appData.params.first()
        checkNotNull(params)
        val spec = GCMParameterSpec(TAG_BIT_LENGTH, params.iv)
        cipher.init(Cipher.DECRYPT_MODE, key, spec)
        cipher.updateAAD(params.aad)
        return cipher.doFinal(data)!!
    }

    companion object {
        private const val AAD_LENGTH = 16
        private const val TAG_BIT_LENGTH = 16 * 8
        private const val KEY_ALGORITHM = "AES"
        private const val KEY_TRANSFORMATION = "AES/GCM/NoPadding"
        private const val KEYSTORE_PROVIDER = "AndroidKeyStore"
        private const val KEY_ALIAS = "AppData"
    }
}
