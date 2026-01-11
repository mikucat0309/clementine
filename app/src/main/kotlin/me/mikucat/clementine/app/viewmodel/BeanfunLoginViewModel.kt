package me.mikucat.clementine.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import me.mikucat.clementine.BeanfunAccount
import me.mikucat.clementine.app.repo.PlayAPIRepo

class BeanfunLoginViewModel(
    private val api: PlayAPIRepo,
) : ViewModel() {
    private val _error = MutableSharedFlow<Throwable>(
        replay = 0,
        extraBufferCapacity = 1,
    )
    val error = _error.asSharedFlow()
    private val _success = MutableSharedFlow<String>(
        replay = 0,
        extraBufferCapacity = 1,
    )
    val success = _success.asSharedFlow()
    private val _isFetching = MutableStateFlow(false)
    val isFetching = _isFetching.asStateFlow()
    private val _accounts = MutableStateFlow(emptyList<BeanfunAccount>())
    val accounts = _accounts.asStateFlow()
    private val _isLogin = MutableStateFlow(false)
    val isLogin = _isLogin.asStateFlow()

    fun fetchAccounts() {
        viewModelScope.launch {
            _isFetching.tryEmit(true)
            api.getBeanfunAccounts()
                .onSuccess {
                    _accounts.value = it
                }
                .onFailure {
                    _error.tryEmit(it)
                }
            _isFetching.tryEmit(false)
        }
    }

    fun login(account: BeanfunAccount, token: String) {
        viewModelScope.launch {
            _isLogin.tryEmit(true)
            api.beanfunLogin(account, token)
                .onSuccess {
                    _success.tryEmit(account.nick)
                }
                .onFailure {
                    _error.tryEmit(it)
                }
            _isLogin.tryEmit(false)
        }
    }
}
