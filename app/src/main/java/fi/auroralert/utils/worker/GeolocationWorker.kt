package fi.auroralert.utils.worker

import android.content.Context
import android.content.pm.PackageManager
import android.location.Criteria
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.util.Log
import androidx.work.Worker
import fi.auroralert.R
import fi.auroralert.model.AuroraDB
import fi.auroralert.model.Geolocation
import fi.auroralert.view.MainActivity
import fi.auroralert.view.TAG
import java.util.*

class GeolocationWorker: Worker(), LocationListener {

    //private var locationManager: LocationManager? = null
   // private val locationListener: LocationListener = object: LocationListener {

        override fun onLocationChanged(p0: Location?) {
            Log.d(TAG, "location? lat: " + p0?.latitude + " lon: " + p0?.longitude)
        }
        override fun onStatusChanged(p0: String?, p1: Int, p2: Bundle?) {}

        override fun onProviderEnabled(p0: String?) {}

        override fun onProviderDisabled(p0: String?) {}

    //}

    override fun doWork(): WorkerResult {
        Log.d(TAG, "geolocation work")

        val db = AuroraDB.get(applicationContext).geolocationDAO()
        val sp = PreferenceManager.getDefaultSharedPreferences(applicationContext)
        val permissions = Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission(applicationContext,
                        android.Manifest.permission.ACCESS_COARSE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(applicationContext,
                        android.Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED

        if (!sp.getBoolean("pref_loc_on", false) || permissions) {
            Log.d(TAG, "geolocation: permission denied or disabled from shared preferences")
            if (permissions){
                // TODO: fix ask permission
                ActivityCompat.requestPermissions(MainActivity(), arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), 0)
            }
            if (db.count() > 0)
                db.delete(db.getAll().value!!)
            db.insert(Geolocation(
                    Date(),
                    sp.getString("pref_loc_lat",
                            applicationContext.resources.getString(R.string.pref_loc_lat_def)).toFloat(),
                    sp.getString("pref_loc_lon",
                            applicationContext.resources.getString(R.string.pref_loc_lon_def)).toFloat()))
            return WorkerResult.FAILURE
        }

        Log.d(TAG, "geolocation: will start to listen to location changes")
        //if(locationManager == null) locationManager = applicationContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val locationManager = applicationContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        //val criteria = Criteria()
        //criteria.setAccuracy(Criteria.ACCURACY_COARSE)
        //locationManager?.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, (1000 * 60 * 15), (10 * 1000f), locationListener)
        val gps = locationManager.allProviders.contains(LocationManager.NETWORK_PROVIDER) &&
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        val net = (locationManager.allProviders.contains(LocationManager.GPS_PROVIDER) &&
                locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
        Log.d(TAG, "location manager? ${locationManager} and gps? ${gps} or net ${net}")
        if(gps || net) {
            //locationManager.requestSingleUpdate(criteria, this, null)
            Log.d(TAG, "location alive we ride")
            //locationManager.requestLocationUpdates(if(net)LocationManager.NETWORK_PROVIDER else LocationManager.GPS_PROVIDER, (1000 * 60 * 15), (10 * 1000f), this)
            locationManager.requestLocationUpdates(if(net)LocationManager.NETWORK_PROVIDER else LocationManager.GPS_PROVIDER, 0, 0f, this)
            Log.d(TAG, "location alive we ride REALLY?????")
            if(locationManager != null) {
                val loc = locationManager.getLastKnownLocation(if(net) LocationManager.NETWORK_PROVIDER else LocationManager.GPS_PROVIDER );
                Log.d(TAG, "will distroy everything? lat: ${loc?.latitude} and lon: ${loc?.longitude}")
                return WorkerResult.SUCCESS
            }
            //val loc = locationManager?.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
            //Log.d(TAG, "location last known? lat : ${loc?.latitude} lon: ${loc?. latitude}")

            Log.d(TAG, "WTF??????????????????????????????????")
            return WorkerResult.FAILURE
        }

        Log.d(TAG, "fucking location!!!!!!!!!!")
        return WorkerResult.FAILURE
    }

}
