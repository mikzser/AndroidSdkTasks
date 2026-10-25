package org.example.androidsdktasks

import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.ContactsContract
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

    private fun loadContacts(){

        val contactsUri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI

        val projection = arrayOf(
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
            ContactsContract.CommonDataKinds.Phone.NUMBER
        )

        val sortOrder = "${ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME} ASC"

        val contactsBuilder = StringBuilder()
        var count = 0

        val cursor = contentResolver.query(contactsUri, projection, null, null, sortOrder)

        cursor?.use { c->

            val nameIndex = c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
            val numberIndex = c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)

            while (c.moveToNext()) {
                count++
                val name = if (nameIndex != - 1) c.getString(nameIndex) else "Без имени"
                val number = if (nameIndex != - 1) c.getString(numberIndex) else "Без номера"

                contactsBuilder.append("$count. $name: $number\n")
            }

            if (count > 0){
                binding.tvContactsList.text = contactsBuilder.toString()
                Toast.makeText(this, "Загружено контактов: $count", Toast.LENGTH_SHORT).show()
            } else {
                binding.tvContactsList.text = "Контакты не найдены"
            }

        }
    }
}