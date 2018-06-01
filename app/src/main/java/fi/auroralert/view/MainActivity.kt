package fi.auroralert.view

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import androidx.work.*
import fi.auroralert.R
import fi.auroralert.model.GeophysicalActivityWorker
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val constraint = Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()
        val updateWork = PeriodicWorkRequest
                .Builder(GeophysicalActivityWorker::class.java, 15, TimeUnit.MINUTES)
                .setConstraints(constraint)
                .build()
        WorkManager.getInstance().enqueue(updateWork)
    }

}
