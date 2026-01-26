package me.mikucat.clementine.app.data.provider

import android.content.Context
import android.content.pm.verify.domain.DomainVerificationManager
import android.content.pm.verify.domain.DomainVerificationUserState

object PermissionProvider {
    fun isAppLinkSelected(ctx: Context, packageName: String, domain: String): Boolean {
        val manager = ctx.getSystemService(DomainVerificationManager::class.java)
        val userState = manager.getDomainVerificationUserState(packageName)
        val state = userState?.hostToStateMap[domain]
        return state == DomainVerificationUserState.DOMAIN_STATE_SELECTED
    }
}
