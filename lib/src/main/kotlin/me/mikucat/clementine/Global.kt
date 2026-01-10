package me.mikucat.clementine

import kotlinx.serialization.json.Json

const val STATE_SIZE = 32
const val NONCE_SIZE = 32
const val UID_SIZE = 16

internal val DefaultJSON = Json {
    ignoreUnknownKeys = true
    encodeDefaults = true
}
internal val HEX_PATTERN = Regex("""[0-9A-Fa-f]+""")
