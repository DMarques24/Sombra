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
open class JsonPreferences(context: Context, name: String) {
    private val prefs = context.getSharedPreferences(name, Context.MODE_PRIVATE)
    private val json = Json { ignoreUnknownKeys = true }

    // null se não houver nada guardado ou se o formato mudou entre versões da app
    fun <T> read(key: String, serializer: KSerializer<T>): T? =
        prefs.getString(key, null)?.let { runCatching { json.decodeFromString(serializer, it) }.getOrNull() }

    fun <T> write(key: String, serializer: KSerializer<T>, value: T?) = prefs.edit {
        if (value == null) remove(key) else putString(key, json.encodeToString(serializer, value))
    }
}

// Os dados da app (perfil, histórico, cache do tempo)
@Singleton
class LocalStore @Inject constructor(
    @ApplicationContext context: Context,
) : JsonPreferences(context, "sombra")

// Os tokens da sessão do Supabase, num ficheiro à parte: fica fora das cópias de segurança
// do Google (ver backup_rules.xml), para uma sessão não aparecer noutro telemóvel
@Singleton
class AuthStore @Inject constructor(
    @ApplicationContext context: Context,
) : JsonPreferences(context, "sombra_auth")
