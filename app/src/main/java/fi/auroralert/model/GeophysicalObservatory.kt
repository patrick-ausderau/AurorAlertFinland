package fi.auroralert.model

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import org.jsoup.Jsoup

data class GeophysicalObservatory(
        val name: String,
        val latitude: Double,
        val longitude: Double) {

    override fun toString(): String = "Name: $name, Lat: $latitude, Lon: $longitude"

}

class GeophysicalObservatoryModel(): ViewModel() {

    private val geoLocations: MutableLiveData<List<GeophysicalObservatory>> = MutableLiveData()

    fun setGeoLocations() {
        //TODO: parse 1st time (or if new stations), otherwise get from room DB
        doAsync {
            val bg = parseGeophysicsObservatories()
            uiThread { geoLocations.value = bg }
        }
    }

    fun getGeoLocations() = geoLocations
}

fun parseGeophysicsObservatories(): List<GeophysicalObservatory> {
    var count = 0
    val lst = mutableListOf<GeophysicalObservatory>()
    Jsoup.connect("http://space.fmi.fi/image/realtime/FIN/").get()
            .select("div#station_map table tr").forEach {
        if(count++ > 1) lst.add(GeophysicalObservatory(
                it.select("td")[0].text(),
                it.select("td")[1].text().toDouble(),
                it.select("td")[2].text().toDouble()))
    }
    return lst
}