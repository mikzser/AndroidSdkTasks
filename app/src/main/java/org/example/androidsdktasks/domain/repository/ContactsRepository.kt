package org.example.androidsdktasks.domain.repository

import org.example.androidsdktasks.domain.model.Contact

interface ContactsRepository{
    fun getContacts(): List<Contact>
}