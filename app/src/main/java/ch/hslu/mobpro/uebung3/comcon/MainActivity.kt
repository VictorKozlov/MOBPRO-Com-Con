package ch.hslu.mobpro.uebung3.comcon

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import kotlinx.android.synthetic.main.activity_main.*
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.lang.Exception
import java.net.HttpURLConnection

class MainActivity : AppCompatActivity(R.layout.activity_main) {

    private val WAITING_TIME_MILIS: Long = 7000
    private var demoThread: Thread = createDemoThread()
    private val bandsViewModel: BandsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnBlock = findViewById<Button>(R.id.btn_gui_block)
        val btnDemo = findViewById<Button>(R.id.btn_thread_start)
        val btnWorker = findViewById<Button>(R.id.btn_worker_start)
        val btnRequest = findViewById<Button>(R.id.btn_get_json)
        val btnResetViewModel = findViewById<Button>(R.id.btn_reset_view_model)

        btnBlock.setOnClickListener {
            freeze7Seconds(it)
        }

        btnWorker.setOnClickListener {
            startDemoWorker(it)
        }

        btnResetViewModel.setOnClickListener{
            bandsViewModel.reset()
        }

        btnRequest.setOnClickListener(){
            bandsViewModel.getBands();
        }

        btnDemo.setOnClickListener(this::startDemoThread)

        bandsViewModel.bands.observe( this, Observer {
            main_nubmer_of_bands.text = "#Bands = ${bandsViewModel.bands.value?.size}" }
        )
    }

    @Suppress("UNUSED_PARAMETER")
    private fun freeze7Seconds(view: View?) {
        try {
            Thread.sleep(WAITING_TIME_MILIS)
        } catch (e: InterruptedException) {
            Toast.makeText(this, "Freeze wurde abgebrochen", Toast.LENGTH_LONG)
        }
    }

    @Suppress("UNUSED_PARAMETER")
    private fun startDemoThread(view: View?) {
        if (!demoThread.isAlive) {
            demoThread = createDemoThread()
            demoThread.start()
            btn_thread_start.text = "[Demo-Thread läuft...]"
        } else {
            Toast.makeText(this, "DemoThread läuft schon!", Toast.LENGTH_LONG)
        }
    }

    private fun createDemoThread(): Thread {
        return object : Thread("HSLUDemoThread") {
            override fun run() = try {
                sleep(WAITING_TIME_MILIS)
                runOnUiThread {
                    btn_thread_start.text = "Demo-Thread starten"
                    Toast.makeText(this@MainActivity, "Demo Thread beendet!", Toast.LENGTH_LONG)
                }
            } catch (e: InterruptedException) {
                //TODO
            }
        }
    }

    @Suppress("UNUSED_PARAMETERS")
    fun startDemoWorker(v: View?) {
        val workManager = WorkManager.getInstance(application)
        val demoWorkerRequest = OneTimeWorkRequestBuilder<DemoWorker>()
                .setInputData(
                        Data.Builder()
                                .putLong(DemoWorker.WAITING_TIME_KEY, WAITING_TIME_MILIS)
                                .build()
                )
                .build()
        workManager.enqueue(demoWorkerRequest)
    }
}
