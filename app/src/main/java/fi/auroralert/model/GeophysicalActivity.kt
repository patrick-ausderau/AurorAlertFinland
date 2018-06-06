package fi.auroralert.model

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import android.arch.persistence.room.Embedded
import android.arch.persistence.room.Entity
import android.arch.persistence.room.ForeignKey
import android.arch.persistence.room.PrimaryKey

@Entity(foreignKeys = [(ForeignKey(
        entity = GeophysicalObservatory::class,
        parentColumns = ["name"],
        childColumns = ["observatory"]))])
data class GeophysicalActivity(
        @PrimaryKey
        val observatory: String,
        val longName: String,
        val rxMax: Int,
        val level: String,
        val updated: String) {

    override fun toString(): String = "$longName ($observatory), RXmax: $rxMax, level: $level"

}

data class GeophysicalActivityLocation(
        @Embedded
        val location: GeophysicalObservatory,
        @Embedded
        val activity: GeophysicalActivity
)

class GeophysicalActivityLocationModel(application: Application): AndroidViewModel(application) {

    private val geoActivityLocation: LiveData<List<GeophysicalActivityLocation>> =
            AuroraDB.get(getApplication()).geoObsActDao().getAll()

    fun getGeoActivityLocation() = geoActivityLocation
}

