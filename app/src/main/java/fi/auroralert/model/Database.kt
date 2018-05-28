package fi.auroralert.model

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Database
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy.REPLACE
import android.arch.persistence.room.Query
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.content.Context

@Database(entities = [(GeophysicalObservatory::class)], version = 1)
abstract class AuroraDB: RoomDatabase() {

    abstract fun geoObsDao(): GeoObsDao

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