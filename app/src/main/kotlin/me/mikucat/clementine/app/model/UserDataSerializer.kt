package me.mikucat.clementine.app.model

import androidx.datastore.core.Serializer
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import me.mikucat.clementine.app.ioDispatcher
import me.mikucat.clementine.app.repo.KeyStoreRepo
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.io.InputStream
import java.io.OutputStream

@Suppress("detekt:UnreachableCode")
object UserDataSerializer : Serializer<UserData>, KoinComponent {
    private val dispatcher: CoroutineDispatcher by inject(ioDispatcher)
    private val json: Json by inject()
    private val keyStore: KeyStoreRepo by inject()
    override val defaultValue: UserData = UserData(null, null)

    override suspend fun readFrom(input: InputStream): UserData {
        return withContext(dispatcher) {
            val cipher = input.readBytes()
            val plain = keyStore.decrypt(cipher)
            val s = plain.decodeToString()
            json.decodeFromString(UserData.serializer(), s)
        }
    }

    override suspend fun writeTo(t: UserData, output: OutputStream) {
        withContext(dispatcher) {
            val s = json.encodeToString(UserData.serializer(), t)
            val plain = s.encodeToByteArray()
            val cipher = keyStore.encrypt(plain)
            output.write(cipher)
        }
    }
}
