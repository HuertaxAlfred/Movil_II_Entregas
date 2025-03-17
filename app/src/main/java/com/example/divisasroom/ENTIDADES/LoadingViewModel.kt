package com.example.divisasroom

import android.app.Application
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.work.WorkInfo
import androidx.work.WorkManager

class LoadingViewModel(application: Application) : AndroidViewModel(application) {
    val isLoading = mutableStateOf(false)

    init {
        observeWorkerStatus()
    }

    /**
     * Mostrar el loading
     */
    private fun observeWorkerStatus() {
        val workManager = WorkManager.getInstance(getApplication())
        workManager.getWorkInfosByTagLiveData("syncExchangeRatesTag").observeForever { workInfos ->
            val isRunning = workInfos.any { it.state == WorkInfo.State.RUNNING }
            isLoading.value = isRunning
        }
    }
}
