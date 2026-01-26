package me.mikucat.clementine.app.repo

import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import me.mikucat.clementine.BeanfunAccount
import me.mikucat.clementine.PlayAPI
import java.net.NetworkInterface

class PlayAPIRepo(
    private val api: PlayAPI,
    private val userData: UserDataRepo,
) {
    private val ip = NetworkInterface.getNetworkInterfaces().asSequence()
        .filter { it.isUp }
        .filterNot { it.isLoopback }
        .filterNot { it.isPointToPoint }
        .filterNot { it.isVirtual }
        .flatMap { it.inetAddresses.asSequence() }
        .filterNot { it.isAnyLocalAddress }
        .filterNot { it.isLinkLocalAddress }
        .filterNot { it.isLoopbackAddress }
        .map { it.hostAddress }
        .firstOrNull() ?: "::1"

    suspend fun getBeanfunAccounts(): Result<List<BeanfunAccount>> {
        val user =
            userData.account.firstOrNull()
                ?: return Result.failure(IllegalStateException("not logged in"))
        return api.getBeanfunAccounts(user, ip)
    }

    suspend fun beanfunLogin(beanfunAcct: BeanfunAccount, token: String): Result<Unit> {
        val gamaAcct =
            userData.account.first()
                ?: return Result.failure(IllegalStateException("not logged in"))
        return api.beanfunLogin(gamaAcct, beanfunAcct, token)
    }
}
