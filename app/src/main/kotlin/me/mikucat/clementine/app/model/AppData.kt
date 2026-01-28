package me.mikucat.clementine.app.model

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonNames

@OptIn(ExperimentalSerializationApi::class)
@Serializable
data class AppData(
    @JsonNames("params")
    val gcmParams: GCMParams? = null,
    val onboarding: Boolean? = null,
)
