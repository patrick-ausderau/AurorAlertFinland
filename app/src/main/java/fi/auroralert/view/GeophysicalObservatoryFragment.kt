package fi.auroralert.view

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import fi.auroralert.R
import fi.auroralert.model.GeophysicalActivityLocation
import fi.auroralert.model.GeophysicalActivityLocationModel
import kotlinx.android.synthetic.main.frag_geophysical_observatory.*
import kotlinx.android.synthetic.main.item_geophysical_observatory.view.*


class GeophysicalObservatoryFragment: Fragment() {

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        rv_geo_obs.layoutManager = LinearLayoutManager(context)

        val goam = ViewModelProviders.of(this).get(GeophysicalActivityLocationModel::class.java)
        goam.getGeoActivityLocation().observe(this, Observer {
            rv_geo_obs.adapter = GeophysicalObservatoryAdapter(it, context)
            txt_geo_date.text = getString(R.string.last_update, it?.firstOrNull()?.activity?.updated ?: "")
        })
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.frag_geophysical_observatory, container, false)
    }

}

class GeophysicalObservatoryAdapter(val items: List<GeophysicalActivityLocation>?, val context: Context?): RecyclerView.Adapter<GeophysicalObservatoryViewHolder>() {
    override fun getItemCount(): Int {
        return items?.size ?: 0
    }

    override fun onBindViewHolder(holder: GeophysicalObservatoryViewHolder, position: Int) {

        holder.geoLevel?.text = context?.resources?.getString(
                R.string.geo_level,
                items?.get(position)?.activity?.rxMax,
                context.resources?.getStringArray(R.array.level)?.get(
                        context.resources?.getStringArray(
                                R.array.level_color)?.indexOf(items?.get(position)?.activity?.level)?:-1) ?: "Failed")
        holder.geoName?.text = context?.resources?.getString(
                R.string.geo_name,
                items?.get(position)?.activity?.longName,
                items?.get(position)?.activity?.observatory)
        holder.geoLat?.text = context?.resources?.getString(
                R.string.geo_latlon,
                items?.get(position)?.location?.latitude,
                items?.get(position)?.location?.longitude)

        holder.geoLevel?.setBackgroundColor(Color.parseColor(items?.get(position)?.activity?.level))

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GeophysicalObservatoryViewHolder {
        return GeophysicalObservatoryViewHolder(LayoutInflater.from(context).inflate(R.layout.item_geophysical_observatory, parent, false))
    }

}

class GeophysicalObservatoryViewHolder(view: View): RecyclerView.ViewHolder(view) {
    val geoName = view.txt_geo_name
    val geoLevel = view.txt_geo_level
    val geoLat = view.txt_geo_latlon
}