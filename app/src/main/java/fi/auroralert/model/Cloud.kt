package fi.auroralert.model

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import java.util.*

@Entity
data class Cloud(
        val location: String,
        val region: String,
        @PrimaryKey
        val time: Date,
        val cover: Float) {
    override fun toString(): String {
        return "$location/$region: $time => $cover"
    }
}

class CloudModel(application: Application): AndroidViewModel(application) {

    private val cloud: LiveData<List<Cloud>> =
            AuroraDB.get(getApplication()).cloudDao().getAll()

    fun getCloud() = cloud
}
