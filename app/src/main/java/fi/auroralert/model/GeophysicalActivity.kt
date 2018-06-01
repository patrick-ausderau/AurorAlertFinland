package fi.auroralert.model

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import android.arch.persistence.room.Embedded
import android.arch.persistence.room.Entity
import android.arch.persistence.room.ForeignKey
import android.arch.persistence.room.PrimaryKey
import android.util.Log
import androidx.work.Worker
import org.jsoup.Jsoup
import java.text.SimpleDateFormat
import java.util.*

const val TAGA = "GeophysicalActivity"

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

class GeophysicalActivityWorker(): Worker() {

    override fun doWork(): WorkerResult {
        Log.d(TAGA, "worker for geo activity " + SimpleDateFormat("HH:mm:ss").format(Date()))
        val db = AuroraDB.get(applicationContext)
        // Check if locations already exist or reload
        var lstObs = db.geoObsDao().getAll()
        Log.d(TAGA, "from db? " + lstObs.size)
        if(lstObs.size <= 1) {
            lstObs = parseGeophysicsObservatories()
            Log.d(TAGA, "then parse web? " + lstObs.size)
            db.geoObsDao().insertAll(lstObs)
        }
        // Check activity
        val lstAct = parseGeophysicsActivity(lstObs)
        if(db.geoActDao().getAll().size <= 1)
            db.geoActDao().insertAll(lstAct)
        else
            db.geoActDao().updateAll(lstAct)

        //TODO: find closest observatory and push an alert if high activity

        return if(lstAct.isNotEmpty()) WorkerResult.SUCCESS else WorkerResult.FAILURE
    }
}

fun parseGeophysicsActivity(lstLoc: List<GeophysicalObservatory>?): List<GeophysicalActivity> {
    val lst = mutableListOf<GeophysicalActivity>()
    var s = "N/A"
    var count = 0 //selector :nth-child don't work?
    val html = Jsoup.connect("http://aurorasnow.fmi.fi/public_service/magforecast_fi.html").get()

    val issued = "Päivitetty "
    //"p:nth-child(2)" :´(
    html.select("p").forEach {
        Log.d(TAGA, "found? " + it.text())
        if(it.text().startsWith(issued)){
            s = it.text().substring(issued.length)
            return@forEach //no break??? And don't exit the loop :(
        }
    }

    //.select("table:nth-child(3) tr").forEach {
    html.select("table").forEach {
        if(count++ == 2) {
            it.select("tr").forEach {fe -> //avoid it -> it problem
                Log.d(TAGA, "found? " + fe.select("td")[0].text())
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