package me.mikucat.clementine.app.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import me.mikucat.clementine.app.data.provider.PermissionProvider
import me.mikucat.clementine.app.repo.AppDataRepo

data class PermissionUIState(
    val gamaPlaySelected: Boolean,
    val selfSelected: Boolean,
)

class OnboardingViewModel(
    private val appDataRepo: AppDataRepo,
) : ViewModel() {
    private val _state = MutableStateFlow(
        PermissionUIState(
            gamaPlaySelected = false,
            selfSelected = false,
        ),
    )
    val state = _state.asStateFlow()

    fun refreshPermission(ctx: Context) {
        val domain = "play.games.gamania.com"
        val gamaPlaySelected =
            PermissionProvider.isAppLinkSelected(ctx, "com.gamania.beanfun", domain)
        val selfSelected = PermissionProvider.isAppLinkSelected(ctx, ctx.packageName, domain)
        _state.value = PermissionUIState(gamaPlaySelected, selfSelected)
    }

    fun disableOnboarding() {
        viewModelScope.launch {
            appDataRepo.updateOnboarding(false)
        }
    }
}
