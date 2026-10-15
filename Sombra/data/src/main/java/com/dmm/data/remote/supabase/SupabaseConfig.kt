package com.dmm.data.remote.supabase

import com.dmm.data.BuildConfig

// Onde está o projeto Supabase. Vazio quando o local.properties não tem os valores.
internal object SupabaseConfig {
    val url: String = BuildConfig.SUPABASE_URL.trimEnd('/')
    val anonKey: String = BuildConfig.SUPABASE_ANON_KEY
    val isConfigured: Boolean get() = url.isNotBlank() && anonKey.isNotBlank()
}
