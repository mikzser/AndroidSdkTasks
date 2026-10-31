package org.example.androidsdktasks

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SecondService :Service(){

    private val serviceJob = Job()
    private val serviceScope = CoroutineScope(Dispatchers.IO + serviceJob)

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "SecondService создан (onCreate)")
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "SecondService запущен (startId: $startId)")
        val notification = createNotification()
        startForeground(NOTIFICATION_ID, notification)
        serviceScope.launch {
            Log.d(TAG, "Начался 40-секундный фоновый таймер...")
            delay(40_000)
            Log.d(TAG, "40 секунд прошло! Открываем MainActivity из Приложения 3...")
            openTask3Activity()
            Log.d(TAG, "SecondService завершает работу через stopSelf($startId)")
            stopSelf(startId)
        }
        return START_NOT_STICKY
    }
    private fun openTask3Activity() {
        try {
            val intent = Intent().apply {
                component = ComponentName(
                    "org.example.androidsdktasks",
                    "org.example.androidsdktasks.MainActivity"
                )
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            startActivity(intent)
            Log.d(TAG, "Activity Задачи 3 успешно запущена!")
        } catch (e: Exception) {
            Log.e(TAG, "Ошибка при запуске Activity: ${e.message}")
        }
    }
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Фоновая служба Задачи 4",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Канал для демонстрации Foreground Service"
            }
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }
    private fun createNotification(): Notification {
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Задача 4: Фоновый сервис активен")
            .setContentText("Идет обратный отсчет 40 секунд до запуска калькулятора...")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setOngoing(true)
            .build()
    }
    override fun onDestroy() {
        super.onDestroy()
        serviceJob.cancel()
        Log.d(TAG, "SecondService уничтожен (onDestroy)")
    }


    companion object {
        private const val TAG = "Task4_SecondService"
        private const val CHANNEL_ID = "task_4_channel"
        private const val NOTIFICATION_ID = 404
    }
}