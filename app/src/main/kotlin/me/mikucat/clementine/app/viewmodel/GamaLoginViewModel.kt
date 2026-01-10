package me.mikucat.clementine.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.ktor.http.Url
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import me.mikucat.clementine.app.repo.AppDataRepo
import me.mikucat.clementine.app.repo.AuthAPIRepo

class GamaLoginViewModel(
    private val api: AuthAPIRepo,
    appData: AppDataRepo,
) : ViewModel() {
    private val _exception = MutableSharedFlow<Throwable>(
        replay = 0,
        extraBufferCapacity = 1,
    )
    val exception = _exception.asSharedFlow()

    private val _isFetching = MutableStateFlow(false)
    val isFetching = _isFetching.asStateFlow()

    val isLoggedIn: StateFlow<Boolean> = appData.account
        .map { it != null }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(),
            false,
        )

    private val _loginURL = MutableSharedFlow<Url>(
        replay = 0,
        extraBufferCapacity = 1,
    )
    val loginURL = _loginURL.asSharedFlow()

    fun genLoginUrl() {
        viewModelScope.launch {
            api.genLoginUrl()
                .onSuccess { _loginURL.emit(it) }
                .onFailure { _exception.tryEmit(it) }
        }
    }

    fun login(state: String?, code: String?) {
        if (state == null || code == null) {
            _exception.tryEmit(IllegalArgumentException("Invalid deep link"))
        } else {
            viewModelScope.launch {
                _isFetching.value = true
                api.login(state, code)
                    .onFailure { _exception.tryEmit(it) }
                _isFetching.value = false
            }
        }
    }
}
