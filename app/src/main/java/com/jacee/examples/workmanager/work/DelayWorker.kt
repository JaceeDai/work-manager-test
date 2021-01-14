package com.jacee.examples.workmanager.work

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.jacee.examples.workmanager.TAG

/**
 * Created by Jacee.
 * Date: 2021.01.09
 */
class DelayWorker(context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {

    override fun doWork(): Result {
        val name = inputData.getString(ARG_NAME)
        if (inputData.getBoolean(ARG_TOAST_START, false) && !name.isNullOrEmpty()) {
            val mainHandler = Handler(Looper.getMainLooper())
            mainHandler.post {
                Toast.makeText(applicationContext, "$name started!", Toast.LENGTH_SHORT).show()
            }
        }
        Log.d(TAG, "[$name] doWork on ${Thread.currentThread().id} started - ${System.currentTimeMillis()}  -> $id")
        // emulated work
        Thread.sleep(inputData.getLong(ARG_TEST_DURATION, 3_000L))
        Log.d(TAG, "[$name] doWork on ${Thread.currentThread().id} ended - ${System.currentTimeMillis()}")
        return Result.success()
    }

    companion object {
        const val ARG_NAME = "name"
        const val ARG_TEST_DURATION = "duration"
        const val ARG_TOAST_START = "start"
    }
}