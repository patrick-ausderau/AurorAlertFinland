package fi.auroralert.model

import android.arch.persistence.room.*
import android.content.Context
import fi.auroralert.utils.Converter

@Database(entities = [(GeophysicalObservatory::class), (GeophysicalActivity::class), (Cloud::class), (Geolocation::class)], version = 1)
@TypeConverters(Converter::class)
abstract class AuroraDB: RoomDatabase() {

    abstract fun geoObsDao(): GeoObsDao
    abstract fun geoActDao(): GeoActDao
    abstract fun geoObsActDao(): GeoObsActDao
    abstract fun cloudDao(): CloudDao
    abstract fun geolocationDAO(): GeolocationDao

    /* one and only one instance */
    companion object {

        private var sInstance: AuroraDB? = null

        @Synchronized
        fun get(context: Context): AuroraDB {
            if (sInstance == null) {
                sInstance = Room
                        .databaseBuilder(context.applicationContext,
                                AuroraDB::class.java,
                                "aurora.db")
                        .build()
            }
            return sInstance!!
        }
    }
}

