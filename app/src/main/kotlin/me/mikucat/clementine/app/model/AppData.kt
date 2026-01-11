package me.mikucat.clementine.app.model

import kotlinx.serialization.Serializable

@Serializable
data class AppData(
    val params: GCMParams?,
)
