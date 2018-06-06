package fi.auroralert.web

import fi.auroralert.BuildConfig
import fi.auroralert.model.CloudXML
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface FmiDataService {

    @GET("/fmi-apikey/" + BuildConfig.FMI_API_KEY + "/wfs?request=getFeature&storedquery_id=fmi::forecast::hirlam::surface::point::multipointcoverage&parameters=totalcloudcover")
    fun cloudCover(
            @Query("latlon") latlon: String,
            @Query("starttime") start: String,
            @Query("endtime") end: String): Call<CloudXML>

}