package org.example.androidsdktasks

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.Looper
import android.os.Message
import android.os.Messenger
import android.os.Handler
import android.os.IBinder
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import org.example.androidsdktasks.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private var serviceMessenger: Messenger? = null
    private var isBound: Boolean = false

    private inner class IncomingHandler : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                SquareService.MSG_SQUARE_RESULT -> {

                    val square = msg.data.getLong(SquareService.KEY_RESULT)
                    val servicePid = msg.data.getInt(SquareService.KEY_PID)
                    val activityPid = android.os.Process.myPid()
                    // Отображаем результат и доказательство для ментора
                    binding.tvResult.text = square.toString()
                    binding.tvProcessInfo.text = "Activity PID: $activityPid\nSquareService PID: $servicePid"
                }
                else -> super.handleMessage(msg)
            }
        }
    }

    private val clientMessenger = Messenger(IncomingHandler())

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            serviceMessenger = Messenger(service)
            isBound = true
            binding.tvProcessInfo.text = "Подключено к сервису в процессе :remote"
        }
        override fun onServiceDisconnected(name: ComponentName?) {
            serviceMessenger = null
            isBound = false
            binding.tvProcessInfo.text = "Связь с сервисом потеряна"
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

        binding.btnCalculate.setOnClickListener {
            calculateSquare()
        }
    }

    override fun onStart() {
        super.onStart()
        val intent = Intent(this, SquareService::class.java)
        bindService(intent, connection, Context.BIND_AUTO_CREATE)
    }
    override fun onStop() {
        super.onStop()
        if (isBound) {
            unbindService(connection)
            isBound = false
        }
    }

    private fun calculateSquare() {

        val input = binding.etNumber.text.toString().trim()

        if (input.isEmpty()) {
            binding.tilNumber.error = "Введите число"
            return
        }
        binding.tilNumber.error = null

        val number = input.toIntOrNull()

        if (number == null) {
            binding.tilNumber.error = "Слишком большое число"
            return
        }

        if (!isBound || serviceMessenger == null) {
            Toast.makeText(this, "Сервис ещё подключается, подождите...", Toast.LENGTH_SHORT).show()
            return
        }


        val msg = Message.obtain(null, SquareService.MSG_CALCULATE_SQUARE, number, 0)

        msg.replyTo = clientMessenger

        serviceMessenger?.send(msg)
    }
}