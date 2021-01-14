package com.jacee.examples.workmanager

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.work.*
import com.jacee.examples.workmanager.databinding.ActivityWorkBinding
import com.jacee.examples.workmanager.work.DelayWorker
import com.jacee.examples.workmanager.work.FailedWorker
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

        binding.startChain.setOnClickListener {
            scheduleChain()
        }

        binding.startUniqueChain.setOnClickListener {
            scheduleUniqueChain()
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

    @SuppressLint("EnqueueWork")
    private fun scheduleChain() {
        val request = OneTimeWorkRequestBuilder<DelayWorker>()
            .setInputData(
                Data.Builder()
                    .putString(DelayWorker.ARG_NAME, "1")
                    .build()
            )
            .build()
        val request2 = OneTimeWorkRequestBuilder<DelayWorker>()
            .setInputData(
                Data.Builder()
                    .putString(DelayWorker.ARG_NAME, "2")
                    .putLong(DelayWorker.ARG_TEST_DURATION, 5_000L)
                    .build()
            )
            .build()
        val request3 = OneTimeWorkRequestBuilder<DelayWorker>()
            .setInputData(
                Data.Builder()
                    .putString(DelayWorker.ARG_NAME, "3")
                    .putLong(DelayWorker.ARG_TEST_DURATION, 1_500L)
                    .build()
            )
            .build()
        val request4 = OneTimeWorkRequestBuilder<FailedWorker>().build()
        val request5 = OneTimeWorkRequestBuilder<DelayWorker>()
            .setInputData(
                Data.Builder()
                    .putString(DelayWorker.ARG_NAME, "5")
                    .putLong(DelayWorker.ARG_TEST_DURATION, 2_000L)
                    .build()
            )
            .build()
        val request6 = OneTimeWorkRequestBuilder<DelayWorker>()
            .setInputData(
                Data.Builder()
                    .putString(DelayWorker.ARG_NAME, "6")
                    .putLong(DelayWorker.ARG_TEST_DURATION, 1_000L)
                    .build()
            )
            .build()
        Log.d(TAG, "chain enqueue on ${Thread.currentThread().id} ${System.currentTimeMillis()}")

        /*WorkManager.getInstance(applicationContext)
            .beginWith(request)
            .then(request4)
            .then(request2)
            .then(request3)
            .apply {
                workInfosLiveData.observe({lifecycle}) { l ->
                    l.forEach {
                        Log.d(TAG, "chain live: ${it.id}: ${it.state}")
                    }
                    Log.d(TAG, "----------------")
                }
            }
            .enqueue()*/

        WorkContinuation.combine(arrayListOf(
            WorkManager.getInstance(applicationContext)
                .beginWith(request2)
                .then(request),
            WorkManager.getInstance(applicationContext)
                .beginWith(request4)
                .then(request3),
        ))
            .then(request6)
            .apply {
                workInfosLiveData.observe({lifecycle}) { l ->
                    l.forEach {
                        Log.d(TAG, "chain live combine: ${it.id}: ${it.state}")
                    }
                    Log.d(TAG, "----------------")
                }
            }
            .enqueue()
    }


    private var uniqueIndex = 0

    private fun scheduleUniqueChain() {
        /*WorkManager.getInstance(applicationContext)
            .beginUniqueWork(
                "a_unique_name_of_one",
                ExistingWorkPolicy.REPLACE,
                OneTimeWorkRequestBuilder<DelayWorker>()
                    .setInputData(
                        Data.Builder()
                            .putString(DelayWorker.ARG_NAME, "unique_${++uniqueIndex}").build()
                    )
                    .build()
                    .apply {
                        WorkManager.getInstance(applicationContext).getWorkInfoByIdLiveData(id).observe({ lifecycle }) {
                            Log.d(TAG, "unique chain: ${it?.id ?: return@observe}: ${it.state}")
                        }
                    }
            ).enqueue()*/

        /*WorkManager.getInstance(applicationContext)
            .beginUniqueWork(
                "a_unique_name_of_two",
                ExistingWorkPolicy.APPEND,
                OneTimeWorkRequestBuilder<DelayWorker>()
                    .setInputData(
                        Data.Builder()
                            .putString(DelayWorker.ARG_NAME, "unique_${++uniqueIndex}").build()
                    )
                    .build()
                    .apply {
                        WorkManager.getInstance(applicationContext).getWorkInfoByIdLiveData(id).observe({ lifecycle }) {
                            Log.d(TAG, "unique chain 1: ${it?.id ?: return@observe}: ${it.state}")
                        }
                    }
            )
            .then(OneTimeWorkRequestBuilder<DelayWorker>()
                .setInputData(
                    Data.Builder()
                        .putString(DelayWorker.ARG_NAME, "unique_${++uniqueIndex}").build()
                )
                .build()
                .apply {
                    WorkManager.getInstance(applicationContext).getWorkInfoByIdLiveData(id).observe({ lifecycle }) {
                        Log.d(TAG, "unique chain 2: ${it?.id ?: return@observe}: ${it.state}")
                    }
                })
            .enqueue()*/

        val name = "a_unique_name_of_three"
        WorkManager.getInstance(applicationContext).getWorkInfosForUniqueWorkLiveData(name).observe({ lifecycle }) { l ->
            l.forEach {
                Log.d(TAG, "unique chain: ${it.id}: ${it.state}")
            }
        }
        WorkManager.getInstance(applicationContext)
            .beginUniqueWork(
                name,
                ExistingWorkPolicy.KEEP,
                OneTimeWorkRequestBuilder<DelayWorker>()
                    .setInputData(
                        Data.Builder()
                            .putString(DelayWorker.ARG_NAME, "unique_${++uniqueIndex}").build()
                    )
                    .build()
            )
            .then(
                OneTimeWorkRequestBuilder<DelayWorker>()
                    .setInputData(
                        Data.Builder()
                            .putString(DelayWorker.ARG_NAME, "unique_${++uniqueIndex}").build()
                    )
                    .build()
            )
            .enqueue()
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