package me.mikucat.clementine.app.data.provider

import androidx.camera.core.ImageProxy
import me.mikucat.clementine.parseBeanfunLoginLink
import me.mikucat.clementine.toURL
import me.mikucat.clementine.unwrapAppLink
import zxingcpp.BarcodeReader

object QRCodeAnalyzer {
    private val barcodeReader = BarcodeReader(
        BarcodeReader.Options(
            formats = setOf(BarcodeReader.Format.QR_CODE),
            tryHarder = true,
            tryDownscale = true,
        ),
    )

    fun decode(image: ImageProxy): String? {
        for (factor in 2..4) {
            barcodeReader.options.downscaleFactor = factor
            val token = barcodeReader.read(image)
                .mapNotNull { it.text }
                .mapNotNull { it.toURL() }
                .mapNotNull { it.unwrapAppLink() }
                .firstNotNullOfOrNull { it.parseBeanfunLoginLink() }
            if (token != null) {
                return token
            }
        }
        return null
    }
}
