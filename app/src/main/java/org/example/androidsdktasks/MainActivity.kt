package org.example.androidsdktasks

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import org.example.androidsdktasks.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val notificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            startServicesChain()
        } else {
            Toast.makeText(this, "Разрешение на уведомления необходимо для Foreground Service!", Toast.LENGTH_LONG).show()
            binding.tvStatus.text = "Статус: Разрешение на уведомления отклонено юзером"
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
        binding.btnStartChain.setOnClickListener {
            checkPermissionAndStart()
        }
    }

    private fun checkPermissionAndStart() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val hasPermission = ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
            if (hasPermission) {
                startServicesChain()
            } else {

                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        } else {
            startServicesChain()
        }
    }

    private fun startServicesChain() {
        val intent = Intent(this, FirstService::class.java)
        startService(intent)
        binding.tvStatus.text = "Статус: FirstService запущен (5 сек) ➔ жди плашку SecondService в шторке (40 сек)..."
        Toast.makeText(this, "Цепочка сервисов успешно запущена!", Toast.LENGTH_SHORT).show()
    }
}