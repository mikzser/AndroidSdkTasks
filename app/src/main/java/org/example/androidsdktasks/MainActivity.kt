package org.example.androidsdktasks

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import org.example.androidsdktasks.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val powerReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.action) {
                Intent.ACTION_POWER_CONNECTED -> {
                    Log.i("PowerConnectionReceiver", "Кабель питания подключен")
                    binding.tvStatusIcon.text = "⚡"
                    binding.tvStatusText.text = "Зарядка подключена"
                }
                Intent.ACTION_POWER_DISCONNECTED -> {
                    Log.i("PowerConnectionReceiver", "Кабель питания отключен")
                    binding.tvStatusIcon.text = "\uD83D\uDD0C"
                    binding.tvStatusText.text = "Зарядка отключена"
                }

            }
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    override fun onStart() {
        super.onStart()
        val filter = IntentFilter().apply {
            addAction(Intent.ACTION_POWER_CONNECTED)
            addAction(Intent.ACTION_POWER_DISCONNECTED)
        }

        ContextCompat.registerReceiver(this, powerReceiver, filter, ContextCompat.RECEIVER_EXPORTED)
    }

    override fun onStop() {
        super.onStop()
        unregisterReceiver(powerReceiver)
    }
}