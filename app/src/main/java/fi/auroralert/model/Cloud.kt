package fi.auroralert.model

import org.simpleframework.xml.Element
import org.simpleframework.xml.Path
import org.simpleframework.xml.Root
import java.util.*

data class Cloud(
        val location: String,
        val time: Date,
        val cover: Float
)

//const val path = "/"

@Root(name = "wfs:FeatureCollection/wfs:member/omso:GridSeriesObservation", strict = false)
class CloudXML(
        @field:Path("om:featureOfInterest/sams:SF_SpatialSamplingFeature/sam:sampledFeature/target:LocationCollection/target:member/target:Location")
        @field:Element(name = "target:region")
        var location: String? = null,
        @field:Path("om:result/gmlcov:MultiPointCoverage/gml:rangeSet/gml:DataBlock")
        @field:Element(name = "gml:doubleOrNilReasonTupleList")
        var values: String? = null
) {
    override fun toString(): String {
        return "loc: $location: $values"
    }
}