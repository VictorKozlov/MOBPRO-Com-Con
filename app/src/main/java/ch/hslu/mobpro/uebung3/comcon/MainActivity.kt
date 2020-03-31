package ch.hslu.mobpro.uebung3.comcon

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(R.layout.activity_main) {

    private val WAITING_TIME_MILIS: Long = 7000
    private var demoThread: Thread = createDemoThread()
    private val bandsViewModel: BandsViewModel by viewModels()
    private var btnBlock:Button? = null
    private var btnDemo:Button? = null
    private var btnWorker:Button? = null
    private var btnRequest:Button? = null
    private var btnResetViewModel:Button? = null
    private var btnChooseband:Button? = null
    private var imageView:ImageView? = null
    private var txtBandInfo:TextView? = null
    private var txtBandName:TextView? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnBlock = findViewById<Button>(R.id.btn_gui_block)
        btnDemo = findViewById<Button>(R.id.btn_thread_start)
        btnWorker = findViewById<Button>(R.id.btn_worker_start)
        btnRequest = findViewById<Button>(R.id.btn_get_json)
        btnResetViewModel = findViewById<Button>(R.id.btn_reset_view_model)
        btnChooseband = findViewById<Button>(R.id.btn_choose_band)
        imageView = findViewById<ImageView>(R.id.img_band_picture)
        txtBandInfo = findViewById<TextView>(R.id.main_current_band_info)
        txtBandName = findViewById<TextView>(R.id.main_current_band_name)

        makeUnvisible()

        btnBlock?.setOnClickListener {
            freeze7Seconds(it)
        }

        btnWorker?.setOnClickListener {
            startDemoWorker()
        }

        btnResetViewModel?.setOnClickListener{
            bandsViewModel.resetViewModel()
            makeUnvisible()
        }

        btnRequest?.setOnClickListener(){
            bandsViewModel.getBands();
        }

        btnChooseband?.setOnClickListener(){
            chooseBand();
        }

        btnDemo?.setOnClickListener(this::startDemoThread)

        bandsViewModel.bands?.observe( this, Observer {
            main_nubmer_of_bands.text = "#Bands = ${bandsViewModel.bands?.value?.size}" }
        )

        bandsViewModel.currentBand?.observe(this, Observer {
            makeVisible()
            main_current_band_name.text = "${bandsViewModel.currentBand?.value?.name}"
            main_current_band_info.text = "${bandsViewModel.currentBand?.value?.homeCountry}, Gründung: ${bandsViewModel.currentBand?.value?.foundingYear}"
            Picasso.get().load(bandsViewModel.currentBand?.value?.bestOfCdCoverImageUrl).into(imageView)
            }
        )
    }

    private fun makeVisible(){
        imageView?.visibility = VISIBLE
        txtBandInfo?.visibility = VISIBLE
        txtBandName?.visibility = VISIBLE
    }

    private fun makeUnvisible(){
        imageView?.visibility = GONE
        txtBandInfo?.visibility = GONE
        txtBandName?.visibility = GONE
    }

    private fun chooseBand() {
        bandsViewModel.getBands()
        var builder = AlertDialog.Builder(this)
        val bandsToChoose: Array<String>? = bandsViewModel.getArrayListBands()
        builder.setTitle("Wählen Sie eine Band aus")
        builder?.setItems(bandsToChoose) {_,which ->
             bandsViewModel.getCurrentBand(bandsViewModel.bands?.value?.get(which)?.code)
        }
        builder.create()
        builder.show()
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
    fun startDemoWorker() {
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
