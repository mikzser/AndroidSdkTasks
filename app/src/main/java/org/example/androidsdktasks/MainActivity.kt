package org.example.androidsdktasks

import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import org.example.androidsdktasks.data.ContactsRepositoryImpl
import org.example.androidsdktasks.databinding.ActivityMainBinding
import org.example.androidsdktasks.domain.usecase.GetContactsUseCase

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val getContactsUseCase by lazy {
        GetContactsUseCase(ContactsRepositoryImpl(contentResolver))
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            loadContacts()
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
    }

    private fun checkPermissionAndLoadContacts() {
        val hasPermission =
            ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED
        if (hasPermission) {
            loadContacts()
        } else {
            requestPermissionLauncher.launch(android.Manifest.permission.READ_CONTACTS)
        }
    }
    private fun loadContacts() {
        val contacts = getContactsUseCase()

        if (contacts.isNotEmpty()) {
            val contactsText = contacts.mapIndexed { index, contact ->
                "${index + 1}. ${contact.name}: ${contact.phoneNumber}"
            }.joinToString("\n")
            binding.tvContactsList.text = contactsText
            Toast.makeText(this, "Загружено контактов: ${contacts.size}", Toast.LENGTH_SHORT).show()
        } else {
            binding.tvContactsList.text = "Контакты не найдены"
        }
    }
}