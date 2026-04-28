package kov_p.pixelplayer.core_storage

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import kov_p.pixelplayer.api_storage.SecurePreferences
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec
import androidx.core.content.edit

private const val SECURE_PREFS_NAME = "pixelplayer_secure_credentials"
private const val KEY_ALIAS = "pixelplayer_secure_credentials_key"
private const val ANDROID_KEY_STORE = "AndroidKeyStore"
private const val CIPHER_TRANSFORMATION = "AES/GCM/NoPadding"
private const val GCM_TAG_LENGTH_BITS = 128
private const val PAYLOAD_SEPARATOR = ":"

internal class AndroidSecurePreferences(
    context: Context,
) : SecurePreferences {
    private val prefs = context.getSharedPreferences(SECURE_PREFS_NAME, Context.MODE_PRIVATE)
    private val secretKey: SecretKey by lazy { getOrCreateSecretKey() }

    override suspend fun getString(key: String): String? {
        val payload = prefs.getString(key, null) ?: return null

        return runCatching { decrypt(payload) }
            .getOrElse {
                remove(key)
                null
            }
    }

    override suspend fun updateValue(key: String, value: String?) {
        if (value == null) {
            remove(key)
        } else {
            prefs.edit { putString(key, encrypt(value)) }
        }
    }

    private fun encrypt(value: String): String {
        val cipher = Cipher.getInstance(CIPHER_TRANSFORMATION)
        cipher.init(Cipher.ENCRYPT_MODE, secretKey)

        val cipherText = cipher.doFinal(value.encodeToByteArray())
        return listOf(
            cipher.iv.toBase64(),
            cipherText.toBase64(),
        )
            .joinToString(PAYLOAD_SEPARATOR)
    }

    private fun decrypt(payload: String): String {
        val parts = payload.split(PAYLOAD_SEPARATOR, limit = 2)
        require(parts.size == 2)

        val iv = parts[0].fromBase64()
        val cipherText = parts[1].fromBase64()

        val cipher = Cipher.getInstance(CIPHER_TRANSFORMATION)
        cipher.init(
            Cipher.DECRYPT_MODE,
            secretKey,
            GCMParameterSpec(GCM_TAG_LENGTH_BITS, iv),
        )

        return cipher.doFinal(cipherText).decodeToString()
    }

    private fun remove(key: String) {
        prefs.edit { remove(key) }
    }

    private fun getOrCreateSecretKey(): SecretKey {
        val keyStore = KeyStore.getInstance(ANDROID_KEY_STORE).apply { load(null) }
        val existingKey = (keyStore.getEntry(KEY_ALIAS, null) as? KeyStore.SecretKeyEntry)
            ?.secretKey

        if (existingKey != null) {
            return existingKey
        }

        val keyGenerator = KeyGenerator.getInstance(
            KeyProperties.KEY_ALGORITHM_AES,
            ANDROID_KEY_STORE,
        )
        val keySpec = KeyGenParameterSpec.Builder(
            KEY_ALIAS,
            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT,
        )
            .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
            .setRandomizedEncryptionRequired(true)
            .build()

        return keyGenerator
            .apply { init(keySpec) }
            .generateKey()
    }

    private fun ByteArray.toBase64(): String {
        return Base64.encodeToString(this, Base64.NO_WRAP)
    }

    private fun String.fromBase64(): ByteArray {
        return Base64.decode(this, Base64.NO_WRAP)
    }
}
