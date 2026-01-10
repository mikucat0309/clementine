package me.mikucat.clementine.app.route

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
data class BeanfunLogin(
    val token: String,
) : NavKey
