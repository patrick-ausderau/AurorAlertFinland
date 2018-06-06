package fi.auroralert.worker

import android.content.SharedPreferences
import android.preference.Preference
import android.preference.PreferenceManager
import android.util.Log
import androidx.work.Worker
import fi.auroralert.BuildConfig
import fi.auroralert.model.CloudXML
import fi.auroralert.view.TAG
import fi.auroralert.web.FmiDataService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.simplexml.SimpleXmlConverterFactory
import java.text.SimpleDateFormat
import java.util.*

class CloudWorker: Worker() {

    override fun doWork(): WorkerResult {
        Log.d(TAG, "parse cloud")

        val retrofit = Retrofit.Builder()
                .baseUrl("http://data.fmi.fi")
                .addConverterFactory(SimpleXmlConverterFactory.create())
                .build()
        val api = retrofit.create<FmiDataService>(FmiDataService::class.java)
        val sp = PreferenceManager.getDefaultSharedPreferences(applicationContext)
        Log.d(TAG, "test cloud: " + sp.getString("pref_cloud", "sharedPref FAILED?"))
        val df = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
        df.setTimeZone(TimeZone.getTimeZone("UTC"))
        Log.d(TAG, "test cloud 3: " + df.format(Date(System.currentTimeMillis() + 6 * 3600 * 1000)))
        // &latlon=60.16952,24.93545&starttime=2018-06-06T14:00:00%2B03:00&endtime=2018-06-07T13:00:00%2B03:00
        val call = api.cloudCover(
                sp.getString("pref_loc_lat", "0.0") + "," + sp.getString("pref_loc_lon", "0.0"),
                df.format(Date()),
                df.format(Date(System.currentTimeMillis() + 6 * 3600 * 1000)))
        Log.d(TAG, "try retrofit: " + call.request().url())
        call.enqueue(object: Callback<CloudXML> {
            override fun onFailure(call: Call<CloudXML>?, t: Throwable?) {
                Log.d(TAG, "retrofit failed: " + t?.message)
            }

            override fun onResponse(call: Call<CloudXML>?, response: Response<CloudXML>?) {
                Log.d(TAG, "retrofit: " + response?.body()?.location + " and maybe: " + response?.body()?.values)
            }

        })

        return WorkerResult.SUCCESS
    }

}