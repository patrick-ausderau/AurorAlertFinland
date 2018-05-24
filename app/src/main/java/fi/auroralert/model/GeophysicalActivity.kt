package fi.auroralert.model

data class GeophysicalActivity(
        val name: String,
        val longName: String,
        val rxMax: Int,
        val level: String) {

    override fun toString(): String = "Name: $longName ($name), RXmax: $rxMax, level: $level"

}

//TODO: parse http://aurorasnow.fmi.fi/public_service/magforecast_fi.html