package com.jacee.examples.workmanager.work

import android.content.Context
import android.util.Log
import androidx.work.Data
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.jacee.examples.workmanager.TAG

/**
 * Created by Jacee.
 * Date: 2021.01.09
 */
class FailedWorker(context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {

    override fun doWork(): Result {
        Log.d(TAG, "FailedWorker on ${Thread.currentThread().id} started - ${System.currentTimeMillis()}")
        // emulated work
        Thread.sleep(1_000)
        Log.d(TAG, "FailedWorker on ${Thread.currentThread().id} ended - ${System.currentTimeMillis()}")
        val withResult = inputData.getBoolean(ARG_RESULT, false)
        return if (withResult) Result.failure(
            Data.Builder()
                .putString("result", "I failed!")
                .build()
        ) else Result.failure()
    }

    companion object {
        const val ARG_RESULT = "with_result"
    }
}