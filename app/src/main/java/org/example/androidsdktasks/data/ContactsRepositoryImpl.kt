package org.example.androidsdktasks.data

import android.content.ContentResolver
import android.provider.ContactsContract
import org.example.androidsdktasks.domain.model.Contact
import org.example.androidsdktasks.domain.repository.ContactsRepository

class ContactsRepositoryImpl(private val contentResolver: ContentResolver) : ContactsRepository {
    override fun getContacts(): List<Contact> {
        val contacts = mutableListOf<Contact>()

        val contactsUri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI

        val projection = arrayOf(
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
            ContactsContract.CommonDataKinds.Phone.NUMBER
        )
        val sortOrder = "${ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME} ASC"

        val cursor = contentResolver.query(contactsUri, projection, null, null, sortOrder)
        cursor?.use { c ->

            val nameIndex = c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
            val numberIndex = c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)

            while (c.moveToNext()) {
                val name = if (nameIndex != -1) c.getString(nameIndex) else "Без имени"
                val number = if (numberIndex != -1) c.getString(numberIndex) else "Без номера"

                contacts.add(Contact(name, number))

            }
        }
        return contacts

    }


}