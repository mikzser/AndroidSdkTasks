package org.example.androidsdktasks

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import androidx.core.content.ContextCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class FirstService: Service() {
    private val serviceJob = Job()
    private val serviceScope = CoroutineScope(Dispatchers.IO + serviceJob)

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "FirstService запущен (startId: $startId) на потоке: ${Thread.currentThread().name}")
        serviceScope.launch {
            Log.d(TAG, "Ждем 5 секунд на фоновом потоке...")
            delay(5000)
            Log.d(TAG, "5 секунд прошло! Запускаем Второй сервис (SecondService)...")
            val secondServiceIntent = Intent(this@FirstService, SecondService::class.java)

            ContextCompat.startForegroundService(this@FirstService, secondServiceIntent)
            Log.d(TAG, "FirstService завершил работу и вызывает stopSelf($startId)")
            stopSelf(startId)
        }
        return START_NOT_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        // Отменяем все корутины скоупа, чтобы не утекала память
        serviceJob.cancel()
        Log.d(TAG, "FirstService полностью уничтожен (onDestroy)")
    }



    companion object {
        private const val TAG = "Task4_FirstService"
    }

}