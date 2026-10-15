package com.dmm.data.local

import android.content.Context
import androidx.core.content.edit
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

// Guarda objetos em JSON nas SharedPreferences. Chega para o que a app guarda (perfil, histórico, cache)
// e não precisa de base de dados nem de mais dependências.
// cipher: se existir, o JSON é cifrado antes de ir para o ficheiro (usado para os tokens da sessão)
open class JsonPreferences(context: Context, name: String, private val cipher: StringCipher? = null) {
    private val prefs = context.getSharedPreferences(name, Context.MODE_PRIVATE)
    private val json = Json { ignoreUnknownKeys = true }

    // null se não houver nada guardado, se o formato mudou entre versões da app
    // ou se o valor cifrado já não decifra (aí é como se não houvesse nada)
    fun <T> read(key: String, serializer: KSerializer<T>): T? {
        val stored = prefs.getString(key, null) ?: return null
        val text = if (cipher == null) stored else cipher.decrypt(stored) ?: return null
        return runCatching { json.decodeFromString(serializer, text) }.getOrNull()
    }

    fun <T> write(key: String, serializer: KSerializer<T>, value: T?) = prefs.edit {
        if (value == null) {
            remove(key)
        } else {
            val text = json.encodeToString(serializer, value)
            putString(key, cipher?.encrypt(text) ?: text)
        }
    }
}

// Os dados da app (perfil, histórico, cache do tempo)
@Singleton
class LocalStore @Inject constructor(
    @ApplicationContext context: Context,
) : JsonPreferences(context, "sombra")

// Os tokens da sessão do Supabase: cifrados com uma chave do Android Keystore e num ficheiro
// à parte, fora das cópias de segurança do Google (ver backup_rules.xml)
@Singleton
class AuthStore @Inject constructor(
    @ApplicationContext context: Context,
    cipher: StringCipher,
) : JsonPreferences(context, "sombra_auth", cipher)
