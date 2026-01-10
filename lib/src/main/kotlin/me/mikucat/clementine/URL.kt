package me.mikucat.clementine

import io.ktor.http.URLProtocol
import io.ktor.http.Url

fun Url.unwrapAppLink(): Url? {
    return if (isAppLink()) parameters["url"]?.toURL() else this
}

fun Url.parseBeanfunLoginLink(): String? {
    if (!isBeanfunLoginLink()) return null
    val token = decryptBFLoginToken(segments[2])
    return token?.token
}

@Suppress("detekt:UnsafeCallOnNullableType")
fun Url.parseGamaLoginLink(): LoginInfo? {
    if (!isGamaLoginLink()) return null
    return LoginInfo(parameters["state"]!!, parameters["code"]!!)
}

fun Url.isAppLink(): Boolean = protocol == URLProtocol.HTTPS
        && host == "play.games.gamania.com"
        && segments == listOf("deeplink")
        && parameters.contains("url")

fun Url.isGamaLoginLink(): Boolean = protocol == URLProtocol.createOrDefault("gameplapp")
        && host == "gameplhost"
        && segments == listOf("deeplinkLogin", "AuthorizeCallBack")
        && parameters.contains("client_id")
        && parameters.contains("code")
        && parameters.contains("state")

@Suppress("detekt:MagicNumber")
fun Url.isBeanfunLoginLink(): Boolean = protocol == URLProtocol.createOrDefault("beanfunapp")
        && host == "Q"
        && segments.size == 3
        && segments[0] == "gameLogin"
        && segments[1] == "gtw"
        && HEX_PATTERN.matches(segments[2])

fun String.toURL(): Url? {
    return runCatching { Url(this) }.getOrNull()
}
