package me.mikucat.clementine.app.model

import kotlinx.serialization.Serializable
import me.mikucat.clementine.GamaAccount

@Serializable
data class UserData(
    val loginState: String? = null,
    val account: GamaAccount? = null,
)
