package me.mikucat.clementine.app

import android.os.Bundle
import android.view.View
import android.view.ViewTreeObserver
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import me.mikucat.clementine.app.theme.Theme
import me.mikucat.clementine.app.view.NavScreen
import me.mikucat.clementine.app.viewmodel.SplashViewModel
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class MainActivity : ComponentActivity(), ViewTreeObserver.OnPreDrawListener, KoinComponent {
    private val vm: SplashViewModel by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val view = findViewById<View>(android.R.id.content)
        view.viewTreeObserver.addOnPreDrawListener(this)

        setContent {
            Theme {
                val state by vm.state.collectAsStateWithLifecycle()
                state?.let {
                    NavScreen(intent, it.onboarding, it.isLoggedIn)
                }
            }
        }
    }

    override fun onPreDraw(): Boolean {
        return vm.state.value != null
    }
}
