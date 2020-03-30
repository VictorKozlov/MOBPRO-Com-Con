package ch.hslu.mobpro.uebung3.comcon

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters

class DemoWorker(ctx: Context, private val params: WorkerParameters) : Worker(ctx, params) {

    companion object {
        const val WAITING_TIME_KEY = "WaitingTimeKey"
        const val DEFAULT_WAITING_TIME = 0L
    }

    override fun doWork(): Result {
        return try {
            val waitingTime = params.inputData.getLong(WAITING_TIME_KEY, DEFAULT_WAITING_TIME)
            Thread.sleep(waitingTime)
            Result.success()
        } catch (throwable: Throwable) {
            Result.failure()
        }
    }
}