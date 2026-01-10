package me.mikucat.clementine

import io.ktor.resources.Resource
import kotlinx.serialization.Serializable

@Suppress("PropertyName", "detekt:ConstructorParameterNaming")
@Resource("/webapi/GamaPlay")
class Routes {
    @Resource("/CodeToLogin")
    class CodeToLogin(
        @Suppress("unused") val parent: Routes = Routes(),
    ) {
        @Serializable
        data class Request(
            val APPuid: String,
            val GaCode: String,
            val SystemVersion: String = "15",
            val DeviceName: String = "Android",
        )

        @Serializable
        data class Response(
            val BfAPPuid: String,
            val GamaOpenID: String,
            val NickName: String,
            val UserhashKey: String,
            val UserIV: String,
        )
    }

    @Resource("/GetAccountList")
    class GetAccountList(
        @Suppress("unused") val parent: Routes = Routes(),
    ) {
        @Serializable
        data class Request(
            val GamaOpenID: String,
            val IPAddress: String,
            val APPuid: String,
            val BfAPPuid: String,
            val ClientID: String = Env.BEANFUN_CLIENT_ID,
        )

        @Serializable
        data class Response(
            val MainAccountList: List<BeanfunAccount>,
        )
    }

    @Resource("/SafeLogin")
    class SafeLogin(
        @Suppress("unused") val parent: Routes = Routes(),
    ) {
        @Serializable
        data class Request(
            val MainAccountID: String,
            val Token: String,
            val Lat: Float,
            val Lng: Float,
            val APPuid: String,
            val BfAPPuid: String,
            val ClientID: String = Env.BEANFUN_CLIENT_ID,
            val StartGame: Boolean = false,
        )

        @Serializable
        data class Response(
            val Browser: String,
            val Location: String,
            val OperationSystem: String,
            val CreateTime: String,
            val IPAddress: String,
        )
    }
}
