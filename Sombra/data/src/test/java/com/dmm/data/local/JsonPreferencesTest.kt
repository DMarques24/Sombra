package com.dmm.data.local

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.dmm.data.remote.supabase.StoredSession
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

// O Keystore real só existe num telemóvel: aqui usa-se uma cifra falsa (inverte e marca o texto)
// para provar que o JsonPreferences cifra antes de guardar e trata bem o que não decifra
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class JsonPreferencesTest {

    private val context = ApplicationProvider.getApplicationContext<Context>()
    private val session = StoredSession("access-token-123", "refresh-token-456", 1_000L, "user-1", "ana@exemplo.pt")

    private class FakeCipher : StringCipher {
        var failDecrypt = false
        override fun encrypt(plainText: String) = "enc:" + plainText.reversed()
        override fun decrypt(cipherText: String) =
            if (failDecrypt || !cipherText.startsWith("enc:")) null else cipherText.removePrefix("enc:").reversed()
    }

    @Test
    fun `com cifra, o ficheiro não tem os tokens em claro e a leitura devolve a sessão`() {
        val cipher = FakeCipher()
        val store = JsonPreferences(context, "teste_auth", cipher)

        store.write("session", StoredSession.serializer(), session)

        val raw = context.getSharedPreferences("teste_auth", Context.MODE_PRIVATE).getString("session", null)!!
        assertFalse(raw.contains("access-token-123"))
        assertFalse(raw.contains("ana@exemplo.pt"))
        assertEquals(session, store.read("session", StoredSession.serializer()))
    }

    @Test
    fun `se já não decifrar (chave perdida ou ficheiro alterado), é como se não houvesse sessão`() {
        val cipher = FakeCipher()
        val store = JsonPreferences(context, "teste_auth", cipher)
        store.write("session", StoredSession.serializer(), session)

        cipher.failDecrypt = true

        assertNull(store.read("session", StoredSession.serializer()))
    }

    @Test
    fun `uma sessão antiga em claro não é aceite`() {
        context.getSharedPreferences("teste_auth", Context.MODE_PRIVATE).edit()
            .putString("session", """{"accessToken":"a","refreshToken":"r","expiresAtMillis":1,"userId":"u","email":"e"}""")
            .commit()

        assertNull(JsonPreferences(context, "teste_auth", FakeCipher()).read("session", StoredSession.serializer()))
    }

    @Test
    fun `sem cifra (dados que não são segredos) continua a guardar JSON`() {
        val store = JsonPreferences(context, "teste_dados")
        store.write("session", StoredSession.serializer(), session)

        val raw = context.getSharedPreferences("teste_dados", Context.MODE_PRIVATE).getString("session", null)!!
        assertEquals(true, raw.startsWith("{"))
        assertEquals(session, store.read("session", StoredSession.serializer()))
    }
}
