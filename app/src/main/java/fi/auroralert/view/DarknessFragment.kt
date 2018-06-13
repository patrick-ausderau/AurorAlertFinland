package fi.auroralert.view

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import fi.auroralert.R
import java.util.*
import ca.rmen.sunrisesunset.SunriseSunset
import fi.auroralert.model.GeolocationModel
import kotlinx.android.synthetic.main.frag_darkness.*
import java.text.SimpleDateFormat

class DarknessFragment: Fragment() {

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val sp = PreferenceManager.getDefaultSharedPreferences(context)
        val gl = ViewModelProviders.of(this).get(GeolocationModel::class.java)
        gl.getGeolocation().observe(this, Observer {
            val lat = it?.latitude?.toDouble()?: sp.getString("pref_loc_lat", this.resources.getString(R.string.pref_loc_lat_def)).toDouble()
            val lon = it?.longitude?.toDouble()?: sp.getString("pref_loc_lon", this.resources.getString(R.string.pref_loc_lon_def)).toDouble()
            val df = SimpleDateFormat("HH:mm", Locale.US)
            val sun = SunriseSunset.getSunriseSunset(Calendar.getInstance(), lat, lon)
            txt_dark_sun.text = getString(R.string.dark_sun,
                    if(sun != null) df.format(sun[0].time) else "N/A",
                    if(sun != null) df.format(sun[1].time) else "N/A")
            val civ = SunriseSunset.getCivilTwilight(Calendar.getInstance(), lat, lon)
            txt_dark_civil.text = getString(R.string.dark_civil,
                    if(civ != null) df.format(civ[0].time) else "N/A",
                    if(civ != null) df.format(civ[1].time) else "N/A")
            val nat = SunriseSunset.getNauticalTwilight(Calendar.getInstance(), lat, lon)
            txt_dark_nautical.text = getString(R.string.dark_nautical,
                    if(nat != null) df.format(nat[0].time) else "N/A",
                    if(nat != null) df.format(nat[1].time) else "N/A")
            val ast = SunriseSunset.getAstronomicalTwilight(Calendar.getInstance(), lat, lon)
            txt_dark_astronomical.text = getString(R.string.dark_astronomical,
                    if(ast != null) df.format(ast[0].time) else "N/A",
                    if(ast != null) df.format(ast[1].time) else "N/A")
        })


    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.frag_darkness, container, false)
    }

}