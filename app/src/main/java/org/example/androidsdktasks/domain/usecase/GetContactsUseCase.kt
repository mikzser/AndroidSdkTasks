package org.example.androidsdktasks.domain.usecase

import android.provider.ContactsContract
import org.example.androidsdktasks.domain.model.Contact
import org.example.androidsdktasks.domain.repository.ContactsRepository

class GetContactsUseCase(private val repository: ContactsRepository){
    operator fun invoke(): List<Contact> = repository.getContacts()
}