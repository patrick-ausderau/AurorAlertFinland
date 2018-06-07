package fi.auroralert.model

import java.util.*

data class Cloud(
        val location: String,
        val region: String,
        val time: Date,
        val cover: Float
) {
    override fun toString(): String {
        return "$location/$region: $time => $cover"
    }
}
