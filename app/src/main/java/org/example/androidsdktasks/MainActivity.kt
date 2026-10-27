package org.example.androidsdktasks

import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.launch
import org.example.androidsdktasks.databinding.ActivityMainBinding
import org.example.androidsdktasks.di.ServiceLocator
import org.example.androidsdktasks.presentation.ContactsUiState
import org.example.androidsdktasks.presentation.ContactsViewModel

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    // 1. Получаем ViewModel через наш ServiceLocator
    private val viewModel: ContactsViewModel by viewModels {
        ServiceLocator.provideContactsViewModelFactory(applicationContext)
    }

    // 2. Лаунчер проверки разрешений
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            viewModel.loadContacts()
        } else {
            Toast.makeText(this, "Доступ к контактам запрещен", Toast.LENGTH_SHORT).show()
            binding.tvContactsList.text = "Для загрузки контактов, нужно разрешение"
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

        binding.btnLoadContacts.setOnClickListener {
            checkPermissionAndLoadContacts()
        }

        // 3. Подписываемся на состояние ViewModel
        observeUiState()
    }

    private fun checkPermissionAndLoadContacts() {
        val hasPermission =
            ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED
        if (hasPermission) {
            viewModel.loadContacts()
        } else {
            requestPermissionLauncher.launch(android.Manifest.permission.READ_CONTACTS)
        }
    }

    private fun observeUiState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    when (state) {
                        is ContactsUiState.Idle -> {
                            // Начальное состояние экрана до нажатия кнопки
                        }
                        is ContactsUiState.Empty -> {
                            binding.tvContactsList.text = "Контакты не найдены"
                        }
                        is ContactsUiState.Success -> {
                            val contactsText = state.contacts.mapIndexed { index, contact ->
                                "${index + 1}. ${contact.name}: ${contact.phoneNumber}"
                            }.joinToString("\n")

                            binding.tvContactsList.text = contactsText
                            Toast.makeText(
                                this@MainActivity,
                                "Загружено контактов: ${state.contacts.size}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
        }
    }
}