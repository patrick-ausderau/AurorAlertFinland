package fi.auroralert.model

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.MutableLiveData
import android.util.Log
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import org.jsoup.Jsoup

const val TAGA = "GeophysicalActivity"

data class GeophysicalActivity(
        val observatory: GeophysicalObservatory,
        val longName: String,
        val rxMax: Int,
        val level: String) {

    override fun toString(): String = "$longName ($observatory), RXmax: $rxMax, level: $level"

}

class GeophysicalActivityModel(application: Application): AndroidViewModel(application) {

    private val geoActivity: MutableLiveData<List<GeophysicalActivity>> = MutableLiveData()

    fun loadGeoActivity() {
        //TODO: move db/network to repository
        doAsync {
            val db = AuroraDB?.get(getApplication())?.geoObsDao()
            var lstObs = db.getAll()// ?: parseGeophysicsObservatories()
            Log.d(TAGA, "from db? " + lstObs?.size ?: "-1")
            if(lstObs == null || lstObs.size <= 1) {
                //TODO: check internet access :D
                lstObs = parseGeophysicsObservatories()
                Log.d(TAGA, "then parse web? " + lstObs?.size ?: "-1")
                db.insertAll(lstObs)
            }
            val bg = parseGeophysicsActivity(lstObs)
            Log.d(TAGA, "parsed? " + bg)
            uiThread { geoActivity.value = bg }
        }
    }

    fun getGeoActivity() = geoActivity
}

//TODO: parse http://aurorasnow.fmi.fi/public_service/magforecast_fi.html
fun parseGeophysicsActivity(lstLoc: List<GeophysicalObservatory>?): List<GeophysicalActivity> {
    val lst = mutableListOf<GeophysicalActivity>()

    var count = 0 //selector :nth-child don't work?
    Jsoup.connect("http://aurorasnow.fmi.fi/public_service/magforecast_fi.html").get()
            //.select("table:nth-child(3) tr").forEach {
            .select("table").forEach {
                if(count++ == 2) {
                    it.select("tr").forEach {
                        Log.d(TAGA, "found? " + it.select("td")[0].text())
                        val sn = """([A-Z]{3})""".toRegex().find(it.select("td")[0].text())?.value?: "FAILED?"
                        lst.add(GeophysicalActivity(
                                lstLoc?.find { it.name ==  sn} ?: GeophysicalObservatory(
                                        sn,
                                        0.0,
                                        0.0),
                                it.select("td")[0].select("a").text(),
                                it.select("td")[3].text().toInt(),
                                it.select("td")[3].attr("bgcolor")))
                    }
                }
            }
    return lst
}