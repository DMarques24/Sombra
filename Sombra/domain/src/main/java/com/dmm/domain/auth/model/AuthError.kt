package com.dmm.domain.auth.model

// Os erros de autenticação que o ecrã sabe explicar. O :data traduz as respostas do Supabase para isto.
enum class AuthError {
    InvalidCredentials,     // email ou palavra-passe errados
    EmailAlreadyUsed,       // já há uma conta com este email
    WeakPassword,           // o Supabase recusou a palavra-passe (curta demais)
    InvalidEmail,
    EmailNotConfirmed,      // a conta existe mas falta carregar no link do email
    TooManyRequests,
    NoConnection,
    NotConfigured,          // faltam o URL e a chave do Supabase no local.properties
    Unknown,
}
