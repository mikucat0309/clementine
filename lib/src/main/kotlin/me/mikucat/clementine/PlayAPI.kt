package me.mikucat.clementine

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.UserAgent
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.ANDROID
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.resources.Resources
import io.ktor.client.plugins.resources.post
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.URLBuilder
import io.ktor.http.URLProtocol
import io.ktor.http.Url
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import io.ktor.util.appendAll
import kotlin.random.Random

class PlayAPI(
    logLevel: LogLevel = LogLevel.NONE,
) {
    private val http: HttpClient = HttpClient(CIO) {
        install(Logging) {
            logger = Logger.ANDROID
            level = logLevel
        }
        install(Resources)
        install(ContentNegotiation) {
            json(DefaultJSON)
        }
        install(UserAgent) {
            agent = "Dart/3.7 (dart:io)"
        }
        install(DefaultRequest) {
            contentType(ContentType.Application.Json)
            url {
                protocol = URLProtocol.HTTPS
                host = "bfweb.beanfun.com"
            }
        }
    }

    private inline fun <reified T> HttpRequestBuilder.setEncryptedBody(aesKey: AESKey?, body: T) {
        val body = DefaultJSON.encodeToString(body).encodeToByteArray()
        if (aesKey != null) {
            setBody(encryptBodyWith(body, aesKey.key, aesKey.iv))
        } else {
            setBody(encryptBody(body))
        }
    }

    fun genGamaLoginURL(): Result<Triple<String, String, Url>> = runCatching {
        val state = Random.nextBytes(STATE_SIZE).toHexString()
        val nonce = Random.nextBytes(NONCE_SIZE).toHexString()
        val url = URLBuilder("https://accounts.gamania.com/oauth2/authorize").apply {
            parameters.appendAll(
                "response_type" to "code",
                "prompt" to "login",
                "client_id" to Env.GAMA_CLIENT_ID,
                "scope" to "openid,userinfo.phone,userinfo.email,userinfo.profile,backpack",
                "redirect_uri" to "gameplapp://gameplhost/deeplinkLogin/AuthorizeCallBack",
                "state" to state,
                "nonce" to nonce,
                "remember_login_status" to "true",
                "force_migrate" to "true",
                "provider" to "beanfun",
                "provider_migrate_redirect_uri" to "gameplapp://gameplhost/deeplinkLogin/AuthorizeCallBack",
                "required_info" to "phone",
            )
        }.build()
        Triple(state, nonce, url)
    }

    suspend fun gamaLogin(code: String): Result<GamaAccount> = runCatching {
        val appUID = Random.nextBytes(UID_SIZE).toHexString()
        val resp = http.post(Routes.CodeToLogin()) {
            setEncryptedBody(
                null,
                Routes.CodeToLogin.Request(
                    APPuid = appUID,
                    GaCode = code,
                ),
            )
        }.body<Wrapper>().unwrap<Routes.CodeToLogin.Response>()
        GamaAccount(
            gamaAppUID = appUID,
            beanfunAppUID = resp.BfAPPuid,
            openID = resp.GamaOpenID,
            nick = resp.NickName,
            aesKey = AESKey(
                key = resp.UserhashKey,
                iv = resp.UserIV,
            ),
        )
    }

    suspend fun getBeanfunAccounts(
        account: GamaAccount,
        ip: String,
    ): Result<List<BeanfunAccount>> =
        runCatching {
            http.post(Routes.GetAccountList()) {
                setEncryptedBody(
                    account.aesKey,
                    Routes.GetAccountList.Request(
                        GamaOpenID = account.openID,
                        APPuid = account.gamaAppUID,
                        BfAPPuid = account.beanfunAppUID,
                        IPAddress = ip,
                    ),
                )
            }.body<Wrapper>().unwrap<Routes.GetAccountList.Response>()
                .MainAccountList
        }

    suspend fun beanfunLogin(
        gamaAcct: GamaAccount,
        beanfunAcct: BeanfunAccount,
        token: String,
    ): Result<Unit> =
        runCatching {
            val lat = Random.nextDouble(Env.LatitudeRange.first, Env.LatitudeRange.second)
            val lng = Random.nextDouble(Env.LongitudeRange.first, Env.LongitudeRange.second)
            http.post(Routes.SafeLogin()) {
                setEncryptedBody(
                    gamaAcct.aesKey,
                    Routes.SafeLogin.Request(
                        MainAccountID = beanfunAcct.id,
                        Token = token,
                        Lat = lat.toFloat(),
                        Lng = lng.toFloat(),
                        APPuid = gamaAcct.gamaAppUID,
                        BfAPPuid = gamaAcct.beanfunAppUID,
                        ClientID = Env.BEANFUN_CLIENT_ID,
                        StartGame = false,
                    ),
                )
            }.body<Wrapper>().unwrap<Routes.SafeLogin.Response>()
        }
}
