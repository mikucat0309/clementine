package me.mikucat.clementine.app.model

import androidx.datastore.core.Serializer
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import me.mikucat.clementine.app.ioDispatcher
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.io.InputStream
import java.io.OutputStream

@Suppress("detekt:UnreachableCode")
object AppDataSerializer : Serializer<AppData>, KoinComponent {
    private val dispatcher: CoroutineDispatcher by inject(ioDispatcher)
    private val json: Json by inject()
    override val defaultValue: AppData = AppData(null, null)

    override suspend fun readFrom(input: InputStream): AppData {
        return withContext(dispatcher) {
            val bs = input.readBytes()
            val s = bs.decodeToString()
            runCatching {
                json.decodeFromString(
                    AppData.serializer(),
                    s,
                )
            }.getOrDefault(defaultValue)
        }
    }

    override suspend fun writeTo(t: AppData, output: OutputStream) {
        withContext(dispatcher) {
            val s = json.encodeToString(AppData.serializer(), t)
            val bs = s.encodeToByteArray()
            output.write(bs)
        }
    }
}
