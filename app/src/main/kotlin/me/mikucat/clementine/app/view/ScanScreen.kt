package me.mikucat.clementine.app.view

import android.Manifest
import androidx.camera.compose.CameraXViewfinder
import androidx.camera.core.SurfaceRequest
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.debounce
import me.mikucat.clementine.app.route.BeanfunLogin
import me.mikucat.clementine.app.theme.Theme
import me.mikucat.clementine.app.viewmodel.ScanViewModel
import org.koin.androidx.compose.koinViewModel
import kotlin.time.Duration.Companion.milliseconds

@OptIn(ExperimentalPermissionsApi::class, FlowPreview::class)
@Composable
fun ScanScreen(
    backStack: NavBackStack<NavKey>,
    vm: ScanViewModel = koinViewModel(),
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraPermissionState = rememberPermissionState(Manifest.permission.CAMERA)

    LaunchedEffect(vm.loginToken) {
        vm.loginToken
            .debounce(100.milliseconds)
            .collect {
                if (backStack.lastOrNull() !is BeanfunLogin) {
                    backStack.add(BeanfunLogin(it))
                }
            }
    }

    LaunchedEffect(cameraPermissionState.status) {
        if (cameraPermissionState.status.isGranted) {
            vm.bindToCamera(context, lifecycleOwner)
        }
    }

    val surfaceRequest by vm.surfaceRequest.collectAsStateWithLifecycle()
    Screen(
        cameraPermissionState,
        surfaceRequest,
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
private fun Screen(
    cameraPermissionState: PermissionState,
    surfaceRequest: SurfaceRequest?,
) {
    Scaffold(
        topBar = { LargeTopAppBar(title = { Text("Scan") }) },
    ) { innerPadding ->
        Box(
            Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .aspectRatio(1.0f)
                .fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            if (cameraPermissionState.status.isGranted) {
                if (surfaceRequest != null) {
                    CameraXViewfinder(
                        surfaceRequest = surfaceRequest,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop,
                    )
                }
            } else {
                Button({ cameraPermissionState.launchPermissionRequest() }) {
                    Text("Open Camera")
                }
            }
        }
    }
}

@Preview
@OptIn(ExperimentalPermissionsApi::class)
@Composable
private fun PreviewScreen() {
    val cameraPermissionState = rememberPermissionState(Manifest.permission.CAMERA)
    Theme(true) {
        Screen(
            cameraPermissionState,
            null,
        )
    }
}
