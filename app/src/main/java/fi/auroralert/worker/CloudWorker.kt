package fi.auroralert.worker

import android.preference.PreferenceManager
import android.util.Log
import androidx.work.Worker
import fi.auroralert.R
import fi.auroralert.view.TAG
import fi.auroralert.web.parseCloudCover

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
                applicationContext.resources.getString(R.string.pref_loc_lat_def)))

        Log.d(TAG, "work cloud: " + lst)
        return if(lst.isNotEmpty()) WorkerResult.SUCCESS else WorkerResult.FAILURE
    }

}