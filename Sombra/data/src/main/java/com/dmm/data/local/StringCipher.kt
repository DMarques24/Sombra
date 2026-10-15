package com.dmm.data.local

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec
import javax.inject.Inject
import javax.inject.Singleton

// Cifra texto para guardar no telemóvel. É uma interface para os testes JVM poderem usar
// uma versão falsa: o Android Keystore só existe num telemóvel (ou emulador) a sério.
interface StringCipher {
    fun encrypt(plainText: String): String

    // null quando não dá para decifrar (chave perdida ao restaurar o telemóvel, ficheiro alterado...)
    fun decrypt(cipherText: String): String?
}

/**
 * AES-256-GCM com a chave no Android Keystore: a chave é gerada e fica dentro do hardware
 * seguro do telemóvel e nunca é lida pela app, que só lhe pede para cifrar e decifrar.
 * O GCM também garante a integridade: um ficheiro alterado não decifra (devolve null).
 * Formato guardado: Base64(IV de 12 bytes + texto cifrado com a tag).
 */
@Singleton
class KeystoreStringCipher @Inject constructor() : StringCipher {

    override fun encrypt(plainText: String): String {
        val cipher = Cipher.getInstance(TRANSFORMATION).apply { init(Cipher.ENCRYPT_MODE, key()) }
        val encrypted = cipher.doFinal(plainText.toByteArray(Charsets.UTF_8))
        return Base64.encodeToString(cipher.iv + encrypted, Base64.NO_WRAP)
    }

    override fun decrypt(cipherText: String): String? = runCatching {
        val bytes = Base64.decode(cipherText, Base64.NO_WRAP)
        val iv = bytes.copyOfRange(0, IV_SIZE)
        val cipher = Cipher.getInstance(TRANSFORMATION).apply {
            init(Cipher.DECRYPT_MODE, key(), GCMParameterSpec(TAG_BITS, iv))
        }
        String(cipher.doFinal(bytes, IV_SIZE, bytes.size - IV_SIZE), Charsets.UTF_8)
    }.getOrNull()

    // A primeira vez gera a chave; depois vai buscá-la ao Keystore
    private fun key(): SecretKey {
        val keyStore = KeyStore.getInstance(ANDROID_KEYSTORE).apply { load(null) }
        (keyStore.getKey(KEY_ALIAS, null) as? SecretKey)?.let { return it }
        return KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, ANDROID_KEYSTORE).apply {
            init(
                KeyGenParameterSpec.Builder(KEY_ALIAS, KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT)
                    .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                    .setKeySize(256)
                    .build()
            )
        }.generateKey()
    }

    private companion object {
        const val ANDROID_KEYSTORE = "AndroidKeyStore"
        const val KEY_ALIAS = "sombra_session_key"
        const val TRANSFORMATION = "AES/GCM/NoPadding"
        const val IV_SIZE = 12
        const val TAG_BITS = 128
    }
}
