package fi.auroralert.model

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.*

@Dao
interface GeoObsDao {

    @Query("SELECT * FROM geophysicalobservatory")
    fun getAll(): List<GeophysicalObservatory>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(geoobs: List<GeophysicalObservatory>)
}

@Dao
interface GeoActDao {

    @Query("SELECT * FROM geophysicalactivity")
    fun getAll(): List<GeophysicalActivity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(geoact: List<GeophysicalActivity>)

    @Update(onConflict = OnConflictStrategy.REPLACE)
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

@Dao
interface CloudDao {

    @Query("SELECT * FROM cloud")
    fun getAll(): LiveData<List<Cloud>>

    @Query("SELECT * FROM cloud WHERE time LIKE :time")
    fun get(time: String): Cloud

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(cloud: List<Cloud>)

    @Query("DELETE FROM cloud")
    fun deleteAll()
}

@Dao
interface GeolocationDao {

    @Query("SELECT * FROM geolocation ORDER BY time")
    fun getAll(): List<Geolocation>

    @Query("SELECT * FROM geolocation ORDER BY time LIMIT 1")
    fun getLast(): LiveData<Geolocation>

    @Query("SELECT count(*) FROM geolocation")
    fun count(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(location: Geolocation)

    @Delete
    fun delete(location: Geolocation)

    @Query("DELETE FROM geolocation")
    fun deleteAll()
}