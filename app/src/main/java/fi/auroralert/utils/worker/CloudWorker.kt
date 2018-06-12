package fi.auroralert.utils.worker

import android.preference.PreferenceManager
import android.util.Log
import androidx.work.Worker
import fi.auroralert.R
import fi.auroralert.model.AuroraDB
import fi.auroralert.view.TAG
import fi.auroralert.utils.web.parseCloudCover

class CloudWorker: Worker() {

    override fun doWork(): WorkerResult {
        Log.d(TAG, "work cloud")

        val sp = PreferenceManager.getDefaultSharedPreferences(applicationContext)
        Log.d(TAG, "work cloud: " + sp.getString("pref_cloud", "sharedPref FAILED?"))
        val db = AuroraDB.get(applicationContext)
        val lastLoc = db.geolocationDAO().getAll().firstOrNull()
        val latDef = applicationContext.resources.getString(R.string.pref_loc_lat_def)
        val lonDef = applicationContext.resources.getString(R.string.pref_loc_lon_def)

        val lat = if(sp.getBoolean("pref_loc_on", false)) lastLoc?.latitude?: latDef
            else sp.getString("pref_loc_lat", latDef)
        val lon = if(sp.getBoolean("pref_loc_on", false)) lastLoc?.longitude?: lonDef
            else sp.getString("pref_loc_lon", lonDef)

        val lst = parseCloudCover("$lat,$lon")

        Log.d(TAG, "work cloud ${sp.getBoolean("pref_loc_on",false)}($lat,$lon): $lst")
        if(lst.isNotEmpty()) {
            db.cloudDao().deleteAll()
            db.cloudDao().insertAll(lst)
            return WorkerResult.SUCCESS
        }

        return WorkerResult.FAILURE
    }

}