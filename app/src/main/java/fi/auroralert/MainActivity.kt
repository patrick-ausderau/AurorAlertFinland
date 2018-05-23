package fi.auroralert

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import fi.auroralert.R
import kotlinx.coroutines.experimental.async
import org.jetbrains.anko.custom.async
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import org.jsoup.Jsoup
import org.jsoup.nodes.Document

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val txtObservatories = findViewById<TextView>(R.id.txt_geo_obs)
        txtObservatories.text = "maybe?"
        doAsync {
            val bg = parseGeophysicsObservatories()
            uiThread {
                txtObservatories.text = bg
            }
        }
    }

    fun parseGeophysicsObservatories(): String {
        val doc = Jsoup.connect("http://space.fmi.fi/image/realtime/FIN/").get()
        var str = "try to parse geophysics observatories"
        var count = 0
        doc.select("div#station_map table tr").forEach {
            str += "\n" + count++
            it.select("td").forEach {
                str += " : " + it.text()
            }
        }
        return str
    }


}
