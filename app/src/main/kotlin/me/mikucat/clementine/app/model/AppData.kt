package me.mikucat.clementine.app.model

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import me.mikucat.clementine.GamaAccount
import me.mikucat.clementine.app.ioDispatcher
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.io.InputStream
import java.io.OutputStream

@Serializable
data class AppData(
    val loginState: String?,
    val account: GamaAccount?,
)

@Suppress("detekt:UnreachableCode")
object AppDataSerializer : Serializer<AppData>, KoinComponent {
    private val dispatcher: CoroutineDispatcher by inject(ioDispatcher)
    private val json: Json by inject()
    override val defaultValue: AppData = AppData(null, null)

    override suspend fun readFrom(input: InputStream): AppData {
        return withContext(dispatcher) {
            val s = input.readBytes().decodeToString()
            try {
                return@withContext json.decodeFromString(AppData.serializer(), s)
            } catch (e: SerializationException) {
                throw CorruptionException("Unable to read Config", e)
            }
        }
    }

    override suspend fun writeTo(t: AppData, output: OutputStream) {
        withContext(dispatcher) {
            output.write(
                json.encodeToString(AppData.serializer(), t).encodeToByteArray(),
            )
        }
    }
}
