package fi.auroralert.utils.worker

import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.preference.PreferenceManager
import android.support.v4.content.ContextCompat
import android.util.Log
import fi.auroralert.R
import fi.auroralert.model.AuroraDB
import fi.auroralert.model.Geolocation
import fi.auroralert.view.TAG
import org.jetbrains.anko.doAsync
import java.util.*

// https://stackoverflow.com/questions/6391902/how-do-i-start-my-app-on-startup
class GeolocationService(): Service(), LocationListener {

    //private var locationManager: LocationManager? = null
    //private val sp: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)

    /*init {
        doAsync {
            doWork()
        }
    }*/

    private fun doWork() {
        Log.d(TAG, "geolocation service")
        val sp = PreferenceManager.getDefaultSharedPreferences(this)
        if (!(sp.getBoolean("pref_loc_on", false)) || (Build.VERSION.SDK_INT >= 23 &&
                        ContextCompat.checkSelfPermission(this,
                                android.Manifest.permission.ACCESS_FINE_LOCATION) !=
                        PackageManager.PERMISSION_GRANTED)) {
            Log.d(TAG, "geolocation: permission denied or disabled from shared preferences")
            /*if (permissions){
                // TODO: fix ask permission
                //ActivityCompat.requestPermissions(MainActivity(), arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), 0)
            }*/
            dbInsert()
            stopSelf()
        }

        Log.d(TAG, "geolocation: will start to listen to location changes")
        val locationManager = this.getSystemService(Context.LOCATION_SERVICE) as LocationManager

        val net = locationManager.allProviders.contains(LocationManager.NETWORK_PROVIDER) &&
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        val gps = locationManager.allProviders.contains(LocationManager.GPS_PROVIDER) &&
                locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        Log.d(TAG, "geolocation manager? ${locationManager} and gps? ${gps} or net ${net}")
        if(gps || net) {
            locationManager.requestLocationUpdates(if(net)LocationManager.NETWORK_PROVIDER else LocationManager.GPS_PROVIDER, 0, 0f, this)
            val loc = locationManager.getLastKnownLocation(if(net) LocationManager.NETWORK_PROVIDER else LocationManager.GPS_PROVIDER )
            Log.d(TAG, "geoloaction Gotcha lat: ${loc?.latitude} and lon: ${loc?.longitude}")
            if(loc != null) dbInsert(loc.latitude, loc.longitude, Date(loc.time)) else dbInsert()

            return
        }

        Log.d(TAG, "geolocation Failed location?")
    }

    private fun dbInsert(latitude: Double? = null, longitude: Double? = null, time: Date = Date()) {

        val sp = PreferenceManager.getDefaultSharedPreferences(this)
        val lat = latitude?.toFloat() ?: sp.getString("pref_loc_lat",
                this.resources.getString(R.string.pref_loc_lat_def)).toFloat()
        val lon = longitude?.toFloat() ?: sp.getString("pref_loc_lon",
                this.resources.getString(R.string.pref_loc_lon_def)).toFloat()
        doAsync {
            val db = AuroraDB.get(applicationContext).geolocationDAO()
            Log.d(TAG, "geolocation db? $db and lat $lat and lon $lon")
            if (db.count() > 0)
                db.deleteAll()
            db.insert(Geolocation(time, lat, lon))
        }
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        doWork()
        return START_NOT_STICKY
    }

    override fun onLocationChanged(p0: Location?) {
        Log.d(TAG, "geolocation? lat: " + p0?.latitude + " lon: " + p0?.longitude)
        // considering near real time, approximate datetime in more than ok
        dbInsert(p0?.latitude, p0?.longitude)
    }
    override fun onStatusChanged(p0: String?, p1: Int, p2: Bundle?) {}

    override fun onProviderEnabled(p0: String?) {}

    override fun onProviderDisabled(p0: String?) {}

}
