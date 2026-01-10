package me.mikucat.clementine.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import me.mikucat.clementine.app.repo.AppDataRepo

class SplashViewModel(
    appData: AppDataRepo,
) : ViewModel() {
    val isLoggedIn: StateFlow<Boolean?> = appData.account
        .map { it != null }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = null,
        )
}
