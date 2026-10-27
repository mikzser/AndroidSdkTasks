package org.example.androidsdktasks.presentation

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.example.androidsdktasks.domain.model.Contact
import org.example.androidsdktasks.domain.usecase.GetContactsUseCase

sealed interface ContactsUiState {
    object Idle : ContactsUiState
    object Empty : ContactsUiState
    data class Success(val contacts: List<Contact>) : ContactsUiState
}

class ContactsViewModel(
    private val getContactsUseCase: GetContactsUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow<ContactsUiState>(ContactsUiState.Idle)
    val uiState: StateFlow<ContactsUiState> = _uiState.asStateFlow()
    fun loadContacts() {
        val contacts = getContactsUseCase()
        if (contacts.isNotEmpty()) {
            _uiState.value = ContactsUiState.Success(contacts)
        } else {
            _uiState.value = ContactsUiState.Empty
        }
    }
}