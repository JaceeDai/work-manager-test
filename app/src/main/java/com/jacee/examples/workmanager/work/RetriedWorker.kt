package com.jacee.examples.workmanager.work

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.jacee.examples.workmanager.TAG

/**
 * Created by Jacee.
 * Date: 2021.01.09
 */
class RetriedWorker(context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {

    override fun doWork(): Result {
        Log.d(TAG, "RetriedWorker on ${Thread.currentThread().id} started - ${System.currentTimeMillis()}")
        // emulated work
        Thread.sleep(1_000)
        Log.d(TAG, "RetriedWorker on ${Thread.currentThread().id} ended - ${System.currentTimeMillis()}")
        val first = inputData.getBoolean(ARG_IS_FIRST, false)
        return if (first) Result.retry() else Result.success()
    }

    companion object {
        const val ARG_IS_FIRST = "is_first_done"
    }
}