package fi.auroralert.model

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import android.content.Context
import android.util.Log
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import org.jsoup.Jsoup

const val TAGO = "GeophysicalObservatory"

@Entity
data class GeophysicalObservatory(
        @PrimaryKey
        val name: String,
        val latitude: Double,
        var longitude: Double) {

    override fun toString(): String = "$name ($latitude, $longitude)"

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