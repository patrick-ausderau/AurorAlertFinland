package fi.auroralert.view

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.work.*
import fi.auroralert.R
import fi.auroralert.worker.CloudWorker
import fi.auroralert.worker.GeophysicalActivityWorker
import kotlinx.android.synthetic.main.activity_main.*
import java.util.concurrent.TimeUnit

const val TAG = "AurorAlert"

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(main_toolbar)

        val constraint = Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()
        val updateGeoActivityWork = PeriodicWorkRequest
                .Builder(GeophysicalActivityWorker::class.java, 15, TimeUnit.MINUTES)
                .setConstraints(constraint)
                .build()
        val updateCloudWork = PeriodicWorkRequest
                .Builder(CloudWorker::class.java, 4, TimeUnit.HOURS)
                .setConstraints(constraint)
                .build()
        WorkManager.getInstance().enqueue(updateGeoActivityWork, updateCloudWork)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {

        if(item?.itemId == R.id.menu_pref){
            startActivity(Intent(this,  SettingsActivity::class.java))
            return true
        }
        return super.onOptionsItemSelected(item)
    }

}
