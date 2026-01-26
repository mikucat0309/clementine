package me.mikucat.clementine.app.view

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.ListItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import me.mikucat.clementine.app.R
import me.mikucat.clementine.app.route.Login
import me.mikucat.clementine.app.viewmodel.OnboardingViewModel
import me.mikucat.clementine.app.viewmodel.PermissionUIState
import org.koin.androidx.compose.koinViewModel

@Composable
fun OnboardingScreen(
    backStack: NavBackStack<NavKey>,
    vm: OnboardingViewModel = koinViewModel(),
) {
    val ctx = LocalContext.current
    LaunchedEffect(vm) {
        vm.refreshPermission(ctx)
    }
    val state by vm.state.collectAsStateWithLifecycle()
    Screen(
        state,
        ctx.packageName,
        onBack = {
            vm.refreshPermission(ctx)
        },
        onDone = {
            vm.disableOnboarding()
            backStack.removeLastOrNull()
            backStack.add(Login(null))
        },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Screen(
    state: PermissionUIState,
    packageName: String,
    onBack: (ActivityResult) -> Unit,
    onDone: () -> Unit,
) {
    Scaffold(
        topBar = { LargeTopAppBar(title = { Text("Setup App Links") }) },
        bottomBar = {
            BottomBar(
                done = !state.gamaPlaySelected && state.selfSelected,
                onClick = onDone,
            )
        },
    ) { innerPadding ->
        Column(
            Modifier
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            PermissionRow(
                "Disable GamaPlay App Links",
                "Must disable it before enable in another app",
                isDone = !state.gamaPlaySelected,
                packageName = "com.gamania.beanfun",
                onBack = onBack,
            )
            PermissionRow(
                "Enable Clementine App Links",
                "Allow Clementine to open app links",
                isDone = state.selfSelected,
                packageName = packageName,
                onBack = onBack,
            )
        }
    }
}

@Composable
private fun BottomBar(
    done: Boolean,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier.padding(16.dp),
        contentAlignment = Alignment.Center,
    ) {
        if (done) {
            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = onClick,
            ) {
                Text("Done")
            }
        } else {
            OutlinedButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = onClick,
            ) {
                Text("Skip")
            }
        }
    }
}

@Composable
private fun PermissionRow(
    headline: String,
    supporting: String,
    isDone: Boolean,
    packageName: String,
    onBack: (ActivityResult) -> Unit,
) {
    val icon = if (isDone) R.drawable.check_box_24px else R.drawable.check_box_outline_blank_24px
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = onBack,
    )
    ListItem(
        headlineContent = { Text(headline) },
        supportingContent = { Text(supporting) },
        trailingContent = { Icon(painterResource(icon), "") },
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable(
                onClick = {
                    val intent = Intent(
                        Settings.ACTION_APP_OPEN_BY_DEFAULT_SETTINGS,
                        Uri.fromParts("package", packageName, null),
                    )
                    launcher.launch(intent)
                },
            ),
    )
}
