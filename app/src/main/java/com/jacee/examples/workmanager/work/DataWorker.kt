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
class DataWorker(context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {

    override fun doWork(): Result {
        val name = inputData.getString(ARG_NAME)
        Log.d(TAG, "[$name] doWork on ${Thread.currentThread().id} started - ${System.currentTimeMillis()}  -> $id")
        // emulated work
        Thread.sleep(1_000L)
        Log.d(TAG, "[$name] doWork on ${Thread.currentThread().id} ended - ${System.currentTimeMillis()}  -> $id")
        return Result.success(Data.Builder()
            .putString(ARG_NAME, "$name ${System.currentTimeMillis()}")
            .build())
    }

    companion object {
        const val ARG_NAME = "name"
    }
}