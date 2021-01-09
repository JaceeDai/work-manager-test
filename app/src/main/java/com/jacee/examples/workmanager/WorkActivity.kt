package com.jacee.examples.workmanager

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.jacee.examples.workmanager.databinding.ActivityWorkBinding
import com.jacee.examples.workmanager.work.DelayWorker

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
    }

    private fun schedule() {
        val request = OneTimeWorkRequestBuilder<DelayWorker>().build()
        Log.d(TAG, "enqueue on ${Thread.currentThread().id} ${System.currentTimeMillis()}")
        WorkManager.getInstance(applicationContext).enqueue(request)
    }
}