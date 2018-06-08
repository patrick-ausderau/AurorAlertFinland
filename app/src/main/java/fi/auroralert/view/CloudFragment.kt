package fi.auroralert.view

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.support.v4.app.Fragment
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import fi.auroralert.R
import fi.auroralert.model.Cloud
import fi.auroralert.model.CloudModel
import kotlinx.android.synthetic.main.frag_cloud.*
import kotlinx.android.synthetic.main.item_cloud.view.*
import java.text.SimpleDateFormat
import java.util.*

class CloudFragment: Fragment() {

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        rv_cloud.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

        val co = ViewModelProviders.of(this).get(CloudModel::class.java)
        co.getCloud().observe(this, Observer {
            txt_cloud.text = getString(R.string.cloud_cover_loc,
                    it?.firstOrNull()?.location ?: "N",
                    it?.firstOrNull()?.region ?: "A")
            rv_cloud.adapter = CloudAdapter(it, context)
        })
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.frag_cloud, container, false)
    }
}


class CloudAdapter(val items: List<Cloud>?, val context: Context?): RecyclerView.Adapter<CloudViewHolder>() {
    override fun getItemCount(): Int {
        return items?.size ?: 0
    }

    override fun onBindViewHolder(holder: CloudViewHolder, position: Int) {

        val df = SimpleDateFormat("HH:mm", Locale.US)
        holder.cloudTime.text = df.format(items?.get(position)?.time)
        holder.cloudVal.text = context?.resources?.getString(R.string.cloud_cover_val, items?.get(position)?.cover)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CloudViewHolder {
        return CloudViewHolder(LayoutInflater.from(context).inflate(R.layout.item_cloud, parent, false))
    }

}

class CloudViewHolder(view: View): RecyclerView.ViewHolder(view) {
    val cloudTime = view.txt_cloud_time
    val cloudVal = view.txt_cloud_percent
}