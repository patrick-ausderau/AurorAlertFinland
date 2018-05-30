package fi.auroralert.view

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.res.TypedArrayUtils
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import fi.auroralert.R
import fi.auroralert.model.GeophysicalActivity
import fi.auroralert.model.GeophysicalActivityModel
import kotlinx.android.synthetic.main.frag_geophysical_observatory.*
import kotlinx.android.synthetic.main.item_geophysical_observatory.view.*


class GeophysicalObservatoryFragment: Fragment() {

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        rv_geo_obs.layoutManager = LinearLayoutManager(context)

        val gom = ViewModelProviders.of(this).get(GeophysicalActivityModel::class.java)
        gom.loadGeoActivity()
        gom.getGeoActivity().observe(this, Observer {
            rv_geo_obs.adapter = GeophysicalObservatoryAdapter(it?.first, context)
            txt_geo_date.text = getString(R.string.last_update, it?.second ?: "")
        })
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.frag_geophysical_observatory, container, false)
    }

}

class GeophysicalObservatoryAdapter(val items: List<GeophysicalActivity>?, val context: Context?): RecyclerView.Adapter<GeophysicalObservatoryViewHolder>() {
    override fun getItemCount(): Int {
        return items?.size ?: 0
    }

    override fun onBindViewHolder(holder: GeophysicalObservatoryViewHolder, position: Int) {
        holder?.geoLongname?.text = items?.get(position)?.longName
        holder?.geoRx?.text = "rx: " + items?.get(position)?.rxMax
        holder?.geoLevel?.text = ", " + (context?.resources?.getStringArray(R.array.level)?.get(context?.resources?.getStringArray(R.array.level_color)?.indexOf(items?.get(position)?.level)?:-1)
                ?: "Failed")
        holder?.geoName?.text = " (" + items?.get(position)?.observatory?.name + "): "
        holder?.geoLat?.text = " (" + items?.get(position)?.observatory?.latitude
        holder?.geoLon?.text = ", " + items?.get(position)?.observatory?.longitude + ")"
        val bgcolor = Color.parseColor(items?.get(position)?.level)
        holder?.geoRx?.setBackgroundColor(bgcolor)
        holder?.geoLevel?.setBackgroundColor(bgcolor)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GeophysicalObservatoryViewHolder {
        return GeophysicalObservatoryViewHolder(LayoutInflater.from(context).inflate(R.layout.item_geophysical_observatory, parent, false))
    }

}

class GeophysicalObservatoryViewHolder(view: View): RecyclerView.ViewHolder(view) {
    val geoLongname = view.txt_geo_longname
    val geoName = view.txt_geo_name
    val geoRx = view.txt_geo_rx
    val geoLevel = view.txt_geo_level
    val geoLat = view.txt_geo_lat
    val geoLon = view.txt_geo_lon
}