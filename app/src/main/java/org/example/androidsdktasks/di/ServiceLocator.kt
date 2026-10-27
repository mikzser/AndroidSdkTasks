package org.example.androidsdktasks.di

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import org.example.androidsdktasks.data.ContactsRepositoryImpl
import org.example.androidsdktasks.domain.repository.ContactsRepository
import org.example.androidsdktasks.domain.usecase.GetContactsUseCase
import org.example.androidsdktasks.presentation.ContactsViewModel

object ServiceLocator{
    fun provideContactsRepository(context: Context): ContactsRepository {
        return ContactsRepositoryImpl(context.contentResolver)
    }
    fun provideGetContactsUseCase(context: Context): GetContactsUseCase {
        return GetContactsUseCase(provideContactsRepository(context))
    }

    fun provideContactsViewModelFactory(context: Context): ViewModelProvider.Factory {
        return object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(ContactsViewModel::class.java)) {
                    @Suppress("UNCHECKED_CAST")
                    return ContactsViewModel(provideGetContactsUseCase(context)) as T
                }
                throw IllegalArgumentException("Unknown ViewModel class")
            }
        }
    }
}