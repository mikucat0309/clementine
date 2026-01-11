package me.mikucat.clementine.app.view

import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import me.mikucat.clementine.app.route.Login
import me.mikucat.clementine.app.route.Scan
import me.mikucat.clementine.app.theme.Theme
import me.mikucat.clementine.app.view.component.MessageDialog
import me.mikucat.clementine.app.viewmodel.GamaLoginViewModel
import org.koin.androidx.compose.koinViewModel


@Composable
fun GamaLoginScreen(
    key: Login,
    backStack: NavBackStack<NavKey>,
    vm: GamaLoginViewModel = koinViewModel(),
) {
    val error = remember { mutableStateOf<Throwable?>(null) }
    LaunchedEffect(vm.error) {
        vm.error.collect {
            error.value = it
        }
    }
    error.value?.let {
        MessageDialog(
            title = it::class.simpleName ?: "Exception",
            text = it.message ?: "Unknown error",
            onDismiss = {
                error.value = null
                backStack.removeLastOrNull()
            },
        )
    }

    val isLoggedIn by vm.isLoggedIn.collectAsStateWithLifecycle()
    LaunchedEffect(isLoggedIn) {
        if (isLoggedIn) {
            backStack.removeAt(backStack.lastIndex)
            backStack.add(Scan)
        }
    }

    val context = LocalContext.current
    LaunchedEffect(vm) {
        vm.loginURL.collect {
            val uri = it.toString().toUri()
            val intent = Intent(Intent.ACTION_VIEW, uri)
            context.startActivity(intent)
        }
    }

    LaunchedEffect(key) {
        key.info?.let { vm.login(it.state, it.code) }
    }

    val isFetching by vm.isFetching.collectAsStateWithLifecycle()
    Screen(isFetching) { vm.genLoginUrl() }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Screen(
    isFetching: Boolean,
    onClickLogin: () -> Unit,
) {
    Scaffold { innerPadding ->
        Box(
            Modifier
                .padding(innerPadding)
                .padding(16.dp),
        ) {
            CenterTitle()
            Column(
                Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Bottom,
            ) {
                ProgressButton("Login", isFetching, onClickLogin)
            }
        }
    }
}

@Preview
@Composable
private fun PreviewScreen() {
    Theme(true) {
        Screen(false) {}
    }
}

@Composable
private fun CenterTitle(modifier: Modifier = Modifier) {
    Column(
        modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = "Clementine",
            style = MaterialTheme.typography.displayLarge,
            color = MaterialTheme.colorScheme.primaryContainer,
        )
    }
}

@Composable
private fun ProgressButton(
    text: String,
    isLoading: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Button(
        onClick = { if (!isLoading) onClick() },
        modifier.fillMaxWidth(),
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(16.dp),
                strokeWidth = 2.dp,
                color = ButtonDefaults.buttonColors().contentColor,
            )
        } else {
            Text(text)
        }
    }
}
