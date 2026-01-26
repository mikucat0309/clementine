package me.mikucat.clementine.app.view

import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import me.mikucat.clementine.app.route.BeanfunLogin
import me.mikucat.clementine.app.route.Login
import me.mikucat.clementine.app.route.Onboarding
import me.mikucat.clementine.app.route.Scan
import me.mikucat.clementine.parseBeanfunLoginLink
import me.mikucat.clementine.parseGamaLoginLink
import me.mikucat.clementine.toURL
import me.mikucat.clementine.unwrapAppLink

@Composable
fun NavScreen(intent: Intent, onboarding: Boolean, isLoggedIn: Boolean) {
    val key = if (onboarding) Onboarding else handleDeepLink(intent, isLoggedIn)
    val backStack = rememberNavBackStack(key)

    NavDisplay(
        backStack = backStack,
        onBack = { backStack.removeLastOrNull() },
        entryProvider = entryProvider {
            entry<Onboarding> {
                OnboardingScreen(backStack)
            }
            entry<Scan> {
                ScanScreen(backStack)
            }
            entry<Login> { key ->
                GamaLoginScreen(key, backStack)
            }
            entry<BeanfunLogin> { key ->
                BeanfunLoginScreen(key, backStack)
            }
        },
    )
}

private fun handleDeepLink(intent: Intent, isLoggedIn: Boolean): NavKey {
    val url = intent.dataString?.toURL()?.unwrapAppLink()

    if (url != null) {
        val info = url.parseGamaLoginLink()
        if (info != null) {
            return Login(info)
        }
        if (isLoggedIn) {
            val token = url.parseBeanfunLoginLink()
            if (token != null) {
                return BeanfunLogin(token)
            }
        }
    }
    return if (isLoggedIn) Scan else Login(null)
}
