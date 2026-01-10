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
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.component.KoinComponent

class MainActivity : ComponentActivity(), ViewTreeObserver.OnPreDrawListener, KoinComponent {

    private val vm: SplashViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val view = findViewById<View>(android.R.id.content)
        view.viewTreeObserver.addOnPreDrawListener(this)

        enableEdgeToEdge()
        setContent {
            Theme {
                val isLoggedIn by vm.isLoggedIn.collectAsStateWithLifecycle()
                isLoggedIn?.let {
                    NavScreen(intent, it)
                }
            }
        }
    }

    override fun onPreDraw(): Boolean {
        return vm.isLoggedIn.value != null
    }
}
