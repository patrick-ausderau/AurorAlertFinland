package fi.auroralert.utils.worker

import android.util.Log
import androidx.work.Worker
import fi.auroralert.model.AuroraDB
import fi.auroralert.view.TAG
import fi.auroralert.utils.web.parseGeophysicsActivity
import fi.auroralert.utils.web.parseGeophysicsObservatories
import java.text.SimpleDateFormat
import java.util.*

class GeophysicalActivityWorker(): Worker() {

    override fun doWork(): WorkerResult {
        Log.d(TAG, "worker for geo activity " + SimpleDateFormat("HH:mm:ss").format(Date()))
        val db = AuroraDB.get(applicationContext)
        // Check if locations already exist or reload
        var lstObs = db.geoObsDao().getAll()
        Log.d(TAG, "from db? " + lstObs.size)
        if(lstObs.size <= 1) {
            lstObs = parseGeophysicsObservatories()
            Log.d(TAG, "then parse web? " + lstObs.size)
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