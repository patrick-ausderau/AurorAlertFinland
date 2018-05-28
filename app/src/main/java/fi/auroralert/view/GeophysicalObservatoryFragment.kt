package fi.auroralert.view

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import fi.auroralert.R
import fi.auroralert.model.GeophysicalObservatory
import fi.auroralert.model.GeophysicalObservatoryModel
import kotlinx.android.synthetic.main.frag_geophysical_observatory.*
import kotlinx.android.synthetic.main.item_geophysical_observatory.view.*


class GeophysicalObservatoryFragment: Fragment() {

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        rv_geo_obs.layoutManager = LinearLayoutManager(context)
        val gom = ViewModelProviders.of(this).get(GeophysicalObservatoryModel::class.java)
        gom.loadGeoLocations()
        gom.getGeoLocations().observe(this, Observer {
            /*val txtGeo = view!!.findViewById<TextView>(R.id.txt_geo_obs)
            txtGeo.text = it.toString()
            */
            rv_geo_obs.adapter = GeophysicalObservatoryAdapter(it, context)
        })
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.frag_geophysical_observatory, container, false)
    }

}

class GeophysicalObservatoryAdapter(val items: List<GeophysicalObservatory>?, val context: Context?): RecyclerView.Adapter<GeophysicalObservatoryViewHolder>() {
    override fun getItemCount(): Int {
        return items?.size ?: 0
    }

    override fun onBindViewHolder(holder: GeophysicalObservatoryViewHolder, position: Int) {
        holder?.geoName?.text = items?.get(position)?.name
        holder?.geoLat?.text = " (" + items?.get(position)?.latitude
        holder?.geoLon?.text = ", " + items?.get(position)?.longitude + ")"
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GeophysicalObservatoryViewHolder {
        return GeophysicalObservatoryViewHolder(LayoutInflater.from(context).inflate(R.layout.item_geophysical_observatory, parent, false))
    }

}

class GeophysicalObservatoryViewHolder(view: View): RecyclerView.ViewHolder(view) {
    val geoName = view.txt_geo_name
    val geoLat = view.txt_geo_lat
    val geoLon = view.txt_geo_lon
}