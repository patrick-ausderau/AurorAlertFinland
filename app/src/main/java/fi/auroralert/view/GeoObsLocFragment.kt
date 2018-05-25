package fi.auroralert.view

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import fi.auroralert.R
import fi.auroralert.model.GeophysicalObservatoryModel


class GeoObsLocFragment: Fragment() {

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val gom = ViewModelProviders.of(this).get(GeophysicalObservatoryModel::class.java)
        gom.loadGeoLocations()
        gom.getGeoLocations().observe(this, Observer {
            val txtGeo = view!!.findViewById<TextView>(R.id.txt_geo_obs)
            txtGeo.text = it.toString()
        })
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.frag_geophysical_observatory_location, container, false)
    }

}