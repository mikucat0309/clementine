package me.mikucat.clementine.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import me.mikucat.clementine.app.repo.AppDataRepo
import me.mikucat.clementine.app.repo.PlayAPIRepo

data class SplashUIState(
    val onboarding: Boolean,
    val isLoggedIn: Boolean,
)

class SplashViewModel(
    private val api: PlayAPIRepo,
    private val appData: AppDataRepo,
) : ViewModel() {
    private val _state = MutableStateFlow<SplashUIState?>(null)
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            val j1 = async {
                appData.onboarding.firstOrNull() ?: true
            }
            val j2 = async {
                api.getBeanfunAccounts().isSuccess
            }
            val onboarding = j1.await()
            val isLoggedIn = j2.await()
            _state.value = SplashUIState(onboarding, isLoggedIn)
        }
    }
}
