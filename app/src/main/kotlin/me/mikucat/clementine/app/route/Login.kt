package me.mikucat.clementine.app.route

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable
import me.mikucat.clementine.LoginInfo

@Serializable
data class Login(
    val info: LoginInfo?,
) : NavKey
