package me.mikucat.clementine

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.decodeFromJsonElement

@Serializable
internal class Wrapper(
    @SerialName("ResultData")
    val data: JsonObject?,
    @SerialName("Result")
    val result: Int,
    @SerialName("ResultMessage")
    val message: String,
) {
    inline fun <reified T> unwrap(): T {
        if (result != 1) throw PlayAPIException(message)
        if (data == null) throw PlayAPIException(message)
        val obj: T = DefaultJSON.decodeFromJsonElement(data)
        return obj
    }
}

@Serializable
internal data class UserEncryptedBody(
    @SerialName("Data")
    val data: String,
    @SerialName("Key")
    val key: String,
)

@Serializable
internal data class EncryptedBody(
    @SerialName("Data")
    val data: String,
)

@Serializable
data class LoginInfo(
    val state: String,
    val code: String,
)

@Serializable
data class GamaAccount(
    val gamaAppUID: String,
    val beanfunAppUID: String,
    val openID: String,
    val nick: String,
    val aesKey: AESKey,
)

@Serializable
data class AESKey(
    val key: String,
    val iv: String,
)

@Serializable
data class BeanfunAccount(
    @SerialName("MainAccountID")
    val id: String,
    @SerialName("NickName")
    val nick: String,
)

@Serializable
data class BeanfunLoginToken(
    val token: String,
)
