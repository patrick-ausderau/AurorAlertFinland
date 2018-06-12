package fi.auroralert.view

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import fi.auroralert.R
import fi.auroralert.model.GeolocationModel
import kotlinx.android.synthetic.main.frag_location.*

class GeolocationFragment: Fragment() {

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val gl = ViewModelProviders.of(this).get(GeolocationModel::class.java)
        gl.getGeolocation().observe(this, Observer {
            txt_loc_time.text = getString(R.string.loc_time, it?.time)
            txt_loc_lat_lon.text = getString(R.string.loc_lat_lon, it?.latitude, it?.longitude)
        })
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.frag_location, container, false)
    }
}