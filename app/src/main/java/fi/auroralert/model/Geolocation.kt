package fi.auroralert.model

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import java.util.*

@Entity
data class Geolocation(
        @PrimaryKey
        val time: Date,
        val latitude: Float,
        val longitude: Float)

class GeolocationModel(application: Application): AndroidViewModel(application) {

    private val geolocation: LiveData<Geolocation> =
            AuroraDB.get(getApplication()).geolocationDAO().getLast()

    fun getGeolocation() = geolocation
}