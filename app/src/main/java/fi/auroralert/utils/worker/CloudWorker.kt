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
        Log.d(TAG, "test cloud: " + sp.getString("pref_cloud", "sharedPref FAILED?"))

        val lst = parseCloudCover(sp.getString(
                "pref_loc_lat",
                applicationContext.resources.getString(R.string.pref_loc_lat_def))
                + "," + sp.getString(
                "pref_loc_lon",
                applicationContext.resources.getString(R.string.pref_loc_lon_def)))

        Log.d(TAG, "work cloud: " + lst)
        if(lst.isNotEmpty()) {
            val db = AuroraDB.get(applicationContext)
            db.cloudDao().deleteAll()
            db.cloudDao().insertAll(lst)
            return WorkerResult.SUCCESS
        }

        return WorkerResult.FAILURE
    }

}