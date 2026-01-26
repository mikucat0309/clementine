package me.mikucat.clementine.app.viewmodel

import android.content.Context
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.core.SurfaceRequest
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.lifecycle.awaitInstance
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.asExecutor
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import me.mikucat.clementine.app.data.provider.QRCodeAnalyzer
import me.mikucat.clementine.app.ioDispatcher
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class ScanViewModel : ViewModel(), ImageAnalysis.Analyzer, KoinComponent {
    private val dispatcher: CoroutineDispatcher by inject(ioDispatcher)

    private val _surfaceRequest = MutableStateFlow<SurfaceRequest?>(null)
    val surfaceRequest = _surfaceRequest.asStateFlow()

    private val _loginToken = MutableSharedFlow<String>(
        replay = 0,
        extraBufferCapacity = 1,
    )

    val loginToken = _loginToken.asSharedFlow()


    fun bindToCamera(context: Context, lifecycleOwner: LifecycleOwner) {
        viewModelScope.launch {
            val provider = ProcessCameraProvider.awaitInstance(context)
            val preview = Preview.Builder().build().apply {
                setSurfaceProvider { request ->
                    _surfaceRequest.update { request }
                }
            }

            val imageAnalysis = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()
                .apply {
                    setAnalyzer(dispatcher.asExecutor(), this@ScanViewModel)
                }

            val camera = provider.bindToLifecycle(
                lifecycleOwner,
                CameraSelector.DEFAULT_BACK_CAMERA,
                preview, imageAnalysis,
            )
            val control = camera.cameraControl
            withContext(dispatcher) {
                control.setZoomRatio(2.0f)
            }
        }
    }

    override fun analyze(image: ImageProxy) {
        QRCodeAnalyzer.decode(image)?.let { _loginToken.tryEmit(it) }
        image.close()
    }
}
