package com.jacee.examples.workmanager

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.jacee.examples.workmanager.databinding.ActivityWorkBinding
import com.jacee.examples.workmanager.work.DelayWorker
import java.time.Duration
import java.util.concurrent.TimeUnit

class WorkActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWorkBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(ActivityWorkBinding.inflate(layoutInflater).let {
            binding = it
            it.root
        })

        binding.start.setOnClickListener {
            schedule()
        }

        binding.repeat.setOnClickListener {
            scheduleRepeat()
        }

        binding.repeatFlex.setOnClickListener {
            scheduleRepeatFlex()
        }
    }

    private fun schedule() {
        val request = OneTimeWorkRequestBuilder<DelayWorker>().build()
        Log.d(TAG, "enqueue on ${Thread.currentThread().id} ${System.currentTimeMillis()}")
        WorkManager.getInstance(applicationContext).enqueue(request)
    }

    private fun scheduleRepeat() {
        val request = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            PeriodicWorkRequestBuilder<DelayWorker>(Duration.ofMillis(1000L)).build()
        } else {
            PeriodicWorkRequestBuilder<DelayWorker>(1000L, TimeUnit.MILLISECONDS).build()
        }
        Log.d(TAG, "enqueue periodic on ${Thread.currentThread().id} ${System.currentTimeMillis()}")
        WorkManager.getInstance(applicationContext).enqueue(request)
    }

    private fun scheduleRepeatFlex() {
        val request = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            PeriodicWorkRequestBuilder<DelayWorker>(Duration.ofMinutes(30), Duration.ofMinutes(20)).build()
        } else {
            PeriodicWorkRequestBuilder<DelayWorker>(30, TimeUnit.MINUTES, 20, TimeUnit.MINUTES).build()
        }
        Log.d(TAG, "enqueue periodic with flex on ${Thread.currentThread().id} ${System.currentTimeMillis()}")
        WorkManager.getInstance(applicationContext).enqueue(request)
    }
}