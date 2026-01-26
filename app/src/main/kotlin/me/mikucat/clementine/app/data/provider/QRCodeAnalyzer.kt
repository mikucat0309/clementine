package me.mikucat.clementine.app.data.provider

import androidx.camera.core.ImageProxy
import me.mikucat.clementine.parseBeanfunLoginLink
import me.mikucat.clementine.toURL
import me.mikucat.clementine.unwrapAppLink
import zxingcpp.BarcodeReader
import kotlin.math.min

object QRCodeAnalyzer {
    private val barcodeReader = BarcodeReader(
        BarcodeReader.Options(
            formats = setOf(BarcodeReader.Format.QR_CODE),
            tryHarder = true,
            tryDownscale = true,
        ),
    )

    fun decode(image: ImageProxy): String? {
        val size = min(image.height, image.width)
        val link = sequenceOf(size / 600, size / 300, size / 100)
            .filter { it >= 1 }
            .map {
                barcodeReader.options.downscaleFactor = it
                barcodeReader.read(image)
            }
            .flatMap { it }
            .mapNotNull { it.text }
            .mapNotNull { it.toURL() }
            .mapNotNull { it.unwrapAppLink() }
            .firstNotNullOfOrNull { it.parseBeanfunLoginLink() }
        return link
    }
}
