package org.example.androidsdktasks.domain.repository

import org.example.androidsdktasks.domain.model.Contact

interface ContactsRepository{
    suspend fun getContacts(): List<Contact>
}