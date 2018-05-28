package fi.auroralert.model

data class GeophysicalActivity(
        val observatory: GeophysicalObservatory,
        val longName: String,
        val rxMax: Int,
        val level: String) {

    override fun toString(): String = "$longName ($observatory), RXmax: $rxMax, level: $level"

}

//TODO: parse http://aurorasnow.fmi.fi/public_service/magforecast_fi.html