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
class DelayWorker(context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {

    override fun doWork(): Result {
        val name = inputData.getString(ARG_NAME)
        Log.d(TAG, "[$name] doWork on ${Thread.currentThread().id} started - ${System.currentTimeMillis()}  -> $id")
        // emulated work
        Thread.sleep(3000)
        Log.d(TAG, "[$name] doWork on ${Thread.currentThread().id} ended - ${System.currentTimeMillis()}")
        return Result.success()
    }

    companion object {
        const val ARG_NAME = "name"
    }
}