package fi.auroralert.utils.web

import android.util.Log
import fi.auroralert.BuildConfig
import fi.auroralert.model.Cloud
import fi.auroralert.view.TAG
import org.jsoup.Jsoup
import org.jsoup.parser.Parser
import java.text.SimpleDateFormat
import java.util.*

fun parseCloudCover(latlon: String): List<Cloud> {

    val lst = mutableListOf<Cloud>()
    val df = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
    df.setTimeZone(TimeZone.getTimeZone("UTC"))
    val now = (3600 * 1000) * (System.currentTimeMillis() / (3600 * 1000))
    Log.d(TAG, "parse cloud: " + df.format(now))

    // &latlon=60.16952,24.93545&starttime=2018-06-06T14:00:00%2B03:00&endtime=2018-06-07T13:00:00%2B03:00
    val url = "http://data.fmi.fi/fmi-apikey/" + BuildConfig.FMI_API_KEY +
            "/wfs?request=getFeature&storedquery_id=fmi::forecast::hirlam::surface::point:" +
            ":multipointcoverage&parameters=totalcloudcover&latlon=" + latlon +
            "&starttime=" + df.format(now) + "&endtime=" +
            df.format(Date( now + 6 * 3600 * 1000))
    val xml = Jsoup.connect(url).parser(Parser.xmlParser()).get()
    //val test = xml.selectFirst("gml:name") ?: "nope"
    val loc = xml.getElementsByTag("gml:name").first().text()
    Log.d(TAG, "parse cloud: " + loc)
    val reg = xml.getElementsByTag("target:region").first().text()
    Log.d(TAG, "parse cloud: " + reg)
    var count = 0
    xml.getElementsByTag("gml:doubleOrNilReasonTupleList").first().text().split(" ").forEach {
        Log.d(TAG, "parse cloud: val " + count + ": " + it)
        lst.add(Cloud(loc, reg, Date( now + (count++) * 3600 * 1000), it.toFloat()))
    }

    return lst
}