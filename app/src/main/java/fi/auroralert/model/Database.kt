package fi.auroralert.model

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.*
import android.arch.persistence.room.OnConflictStrategy.REPLACE
import android.content.Context

@Database(entities = [(GeophysicalObservatory::class), (GeophysicalActivity::class)], version = 1)
abstract class AuroraDB: RoomDatabase() {

    abstract fun geoObsDao(): GeoObsDao
    abstract fun geoActDao(): GeoActDao
    abstract fun geoObsActDao(): GeoObsActDao

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

@Dao
interface GeoObsDao {

    @Query("SELECT * FROM geophysicalobservatory")
    fun getAll(): List<GeophysicalObservatory>

    @Insert(onConflict = REPLACE)
    fun insertAll(geoobs: List<GeophysicalObservatory>)
}

@Dao
interface GeoActDao {

    @Query("SELECT * FROM geophysicalactivity")
    fun getAll(): List<GeophysicalActivity>

    @Insert(onConflict = REPLACE)
    fun insertAll(geoact: List<GeophysicalActivity>)

    @Update(onConflict = REPLACE)
    fun updateAll(geoact: List<GeophysicalActivity>)
}

@Dao
interface GeoObsActDao {

    @Query("SELECT geophysicalobservatory.*, geophysicalactivity.* " +
            "FROM geophysicalobservatory " +
            "INNER JOIN geophysicalactivity " +
            "ON geophysicalobservatory.name = geophysicalactivity.observatory")
    fun getAll(): LiveData<List<GeophysicalActivityLocation>>
}