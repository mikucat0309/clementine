package me.mikucat.clementine.app.model

import kotlinx.serialization.Serializable

@Serializable
data class GCMParams(
    val iv: ByteArray,
    val aad: ByteArray,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as GCMParams

        if (!iv.contentEquals(other.iv)) return false
        if (!aad.contentEquals(other.aad)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = iv.contentHashCode()
        result = 31 * result + aad.contentHashCode()
        return result
    }
}
