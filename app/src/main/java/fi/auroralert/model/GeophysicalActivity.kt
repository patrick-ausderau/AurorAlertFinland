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

    private val geoActivity: MutableLiveData<Pair<List<GeophysicalActivity>, String>> = MutableLiveData()

    fun loadGeoActivity() {
        //TODO: move db/network to repository
        doAsync {
            val db = AuroraDB.get(getApplication()).geoObsDao()
            var lstObs = db.getAll()
            Log.d(TAGA, "from db? " + lstObs.size)
            if(lstObs.size <= 1) {
                //TODO: check internet access :D
                lstObs = parseGeophysicsObservatories()
                Log.d(TAGA, "then parse web? " + lstObs.size)
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
fun parseGeophysicsActivity(lstLoc: List<GeophysicalObservatory>?): Pair<List<GeophysicalActivity>, String> {
    val lst = mutableListOf<GeophysicalActivity>()
    var s = "N/A"

    var count = 0 //selector :nth-child don't work?
    val html = Jsoup.connect("http://aurorasnow.fmi.fi/public_service/magforecast_fi.html").get()
            //.select("table:nth-child(3) tr").forEach {
    html.select("table").forEach {
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

    val issued = "Päivitetty "
    //"p:nth-child(2)" :´(
    html.select("p").forEach {
        Log.d(TAGA, "found? " + it.text())
        if(it.text().startsWith(issued)) s = it.text().substring(issued.length)
    }

    return Pair(lst, s)
}