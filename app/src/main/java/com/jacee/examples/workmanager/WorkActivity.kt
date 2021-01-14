package com.jacee.examples.workmanager

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.work.*
import com.jacee.examples.workmanager.databinding.ActivityWorkBinding
import com.jacee.examples.workmanager.work.DelayWorker
import java.time.Duration
import java.util.*
import java.util.concurrent.TimeUnit

class WorkActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWorkBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(ActivityWorkBinding.inflate(layoutInflater).let {
            binding = it
            it.root
        })

        with(getId()) {
            if (isNotEmpty()) {
                WorkManager.getInstance(applicationContext)
                    .getWorkInfoByIdLiveData(UUID.fromString(this)).observe({ lifecycle }) {
                        Log.d(TAG, "init periodic: ${it.id}: ${it.state}")
                    }
            }
        }

        binding.start.setOnClickListener {
            schedule()
        }

        binding.repeat.setOnClickListener {
            scheduleRepeat()
        }

        binding.repeatStop.setOnClickListener {
            cancelRepeat()
        }

        binding.repeatStopTag.setOnClickListener {
            cancelRepeatByTag()
        }

        binding.repeatFlex.setOnClickListener {
            scheduleRepeatFlex()
        }

    }


    private fun saveId(id: String) {
        getSharedPreferences("data", MODE_PRIVATE).edit().putString("id", id).apply()
    }

    private fun getId(): String {
        return getSharedPreferences("data", MODE_PRIVATE).getString("id", "") ?: ""
    }


    private fun schedule() {
        val request = OneTimeWorkRequestBuilder<DelayWorker>()
            .setInputData(
                Data.Builder()
                    .putString(DelayWorker.ARG_NAME, "ONE-TIME")
                    .build()
            )
            .build()
        Log.d(TAG, "enqueue on ${Thread.currentThread().id} ${System.currentTimeMillis()}")
        WorkManager.getInstance(applicationContext).enqueue(request.also {
            WorkManager.getInstance(applicationContext).getWorkInfoByIdLiveData(it.id).observe({lifecycle}) { info ->
                Log.d(TAG, "onetime: ${info.id}: ${info.state}")
            }
        })
    }

    private fun scheduleRepeat() {
        val request = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            PeriodicWorkRequestBuilder<DelayWorker>(Duration.ofMillis(PeriodicWorkRequest.MIN_PERIODIC_INTERVAL_MILLIS))
                .addTag("test_periodic")
                .build()
        } else {
            PeriodicWorkRequestBuilder<DelayWorker>(PeriodicWorkRequest.MIN_PERIODIC_INTERVAL_MILLIS, TimeUnit.MILLISECONDS)
                .addTag("test_periodic")
                .build()
        }
        Log.d(TAG, "enqueue periodic on ${Thread.currentThread().id} ${System.currentTimeMillis()}")
        binding.repeatStop.tag = request
        saveId(request.id.toString())

        WorkManager.getInstance(applicationContext).getWorkInfoByIdLiveData(request.id).observe({ lifecycle }) {
            Log.d(TAG, "periodic: ${it.id}: ${it.state}")
        }

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


    fun cancelAll(v: View) {
        Log.d(TAG, "cancel all")
        WorkManager.getInstance(applicationContext).cancelAllWork()
        saveId("")
    }


    private fun cancelRepeat() {
        (binding.repeatStop.tag as? PeriodicWorkRequest)?.let { request ->
            Log.d(TAG, "cancel periodic: ${request.id}")
            WorkManager.getInstance(applicationContext).cancelWorkById(request.id)
        }
    }

    private fun cancelRepeatByTag() {
        Log.d(TAG, "cancel periodic by tag")
        WorkManager.getInstance(applicationContext).cancelAllWorkByTag("test_periodic")
    }

}