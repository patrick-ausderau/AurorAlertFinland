package fi.auroralert.model

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import java.util.*

@Entity
data class Geolocation(
        @PrimaryKey
        val time: Date,
        val latitude: Float,
        val longitude: Float)
