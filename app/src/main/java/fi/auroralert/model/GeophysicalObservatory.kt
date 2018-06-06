package fi.auroralert.model

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

@Entity
data class GeophysicalObservatory(
        @PrimaryKey
        val name: String,
        val latitude: Double,
        var longitude: Double) {

    override fun toString(): String = "$name ($latitude, $longitude)"

}
