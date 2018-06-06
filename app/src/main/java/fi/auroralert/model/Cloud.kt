package fi.auroralert.model

import org.simpleframework.xml.Element
import org.simpleframework.xml.Root
import java.util.*

data class Cloud(
        val location: String,
        val time: Date,
        val cover: Float
)

@Root(name = "wfs:FeatureCollection", strict = false)
class CloudXML(
        @field:Element(name = "target:region")
        var location: String? = null,
        @field:Element(name = "gml:doubleOrNilReasonTupleList")
        var values: String? = null
)