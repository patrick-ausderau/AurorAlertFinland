package fi.auroralert.web

import android.util.Log
import fi.auroralert.model.GeophysicalActivity
import fi.auroralert.model.GeophysicalObservatory
import fi.auroralert.view.TAG
import org.jsoup.Jsoup

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

fun parseGeophysicsActivity(lstLoc: List<GeophysicalObservatory>?): List<GeophysicalActivity> {
    val lst = mutableListOf<GeophysicalActivity>()
    var s = "N/A"
    var count = 0 //selector :nth-child don't work?
    val html = Jsoup.connect("http://aurorasnow.fmi.fi/public_service/magforecast_fi.html").get()

    val issued = "Päivitetty "
    //"p:nth-child(2)" :´(
    html.select("p").forEach {
        Log.d(TAG, "found? " + it.text())
        if(it.text().startsWith(issued)){
            s = it.text().substring(issued.length)
            return@forEach //no break??? And don't exit the loop :(
        }
    }

    //.select("table:nth-child(3) tr").forEach {
    html.select("table").forEach {
        if(count++ == 2) {
            it.select("tr").forEach {fe -> //avoid it -> it problem
                Log.d(TAG, "found? " + fe.select("td")[0].text())
                val found = lstLoc?.find {i -> i.name == """([A-Z]{3})""".toRegex().find(fe.select("td")[0].text())?.value?: "FAILED?"}?.name
                if (found != null) lst.add(GeophysicalActivity(
                        found,
                        fe.select("td")[0].select("a").text(),
                        fe.select("td")[3].text().toInt(),
                        fe.select("td")[3].attr("bgcolor"),
                        s))
            }
        } else if(count > 2) return@forEach
    }

    return lst
}