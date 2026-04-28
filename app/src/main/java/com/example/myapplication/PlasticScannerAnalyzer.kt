package com.example.myapplication

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.Rect
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
data class PlasticInfo(
    val name: String,
    val intro: String,
    val location: String,
    val warning: String,
    val steps: String
)

val recyclingData = mapOf(
    "1" to PlasticInfo(
        "PET (1)(Polyethylene Terephthalate)",
        "Highly transparent and lightweight. Mostly used for mineral water, soda, and juice bottles.",
        "Brown Recycling Bins, Reverse Vending Machines (RVMs), \"GREEN@COMMUNITY\".",
        "Single-use only. Do not reuse for hot water or long-term storage as it may release chemicals.",
        "1. Empty liquid.\n" +
                "2. Rinse.\n" +
                "3. Remove cap & label.\n" +
                "4. Flatten."
    ),
    "2" to PlasticInfo(
        "HDPE (2)(High-Density Polyethylene)",
        "A hard, opaque plastic used for milk jugs, shampoo bottles, detergent containers, and toys.",
        "\"GREEN@COMMUNITY\", Housing estate recycling bins.",
        "Generally safe and reusable. Avoid extreme heat to prevent warping.",
        "1. Rinse thoroughly.\n" +
                "2. Remove pump dispensers (often mixed material).\n" +
                "3. Flatten."
    ),
    "3" to PlasticInfo(
        "PVC (3)(Polyvinyl Chloride)",
        "A tough plastic used in clear food packaging, cling wrap, and plumbing pipes.",
        "Special collection points only (Rarely accepted in common bins).",
        "High Toxicity. Contains chlorine; may release harmful toxins if heated or burnt.",
        "1. Check if local \"GREEN@COMMUNITY\" accepts it.\n" +
                "2. Separate from other plastics."
    ),
    "4" to PlasticInfo(
        "LDPE (4)(Low-Density Polyethylene)",
        "Flexible and tough. Used for plastic grocery bags, bread bags, and squeezable bottles.",
        "\"GREEN@COMMUNITY\" (Plastic film collection).",
        "Not heat resistant. Most standard curbside bins do not accept soft films.",
        "1. Ensure it is dry and clean.\n" +
                "2. Remove paper receipts/stickers."
    ),
    "5" to PlasticInfo(
        "PP (5)(Polypropylene)",
        "Strong and heat-resistant (up to 120°C). Used for soy milk bottles, yogurt cups, and microwave lunch boxes.",
        "\"GREEN@COMMUNITY\", Housing estate recycling bins.",
        "Safest choice. Most stable for food/hot drinks. Dishwasher safe.",
        "1. Wash off food residue.\n" +
                "2. Remove any foil seals.\n" +
                "3. Dry before recycling."
    ),
    "6" to PlasticInfo(
        "PS (6)(Polystyrene)",
        "Brittle and cheap. Used for takeaway foam boxes (Styrofoam), yogurt containers, and cup lids.",
        "\"GREEN@COMMUNITY\" (Specific collection bags).",
        "Health Risk. Releases styrene when heated. Avoid microwaving or holding oily hot food.",
        "1. Clean thoroughly (grease is a contaminant).\n" +
                "2. Break into smaller pieces to save space."
    ),
    "7" to PlasticInfo(
        "OTHER (7)(PC, PLA, etc.)",
        "A catch-all category for materials like PC (water cooler jugs) or Bioplastics (PLA).",
        "Large water carboys usually go back to the distributor.",
        "Variable Safety. PC (Polycarbonate) may contain BPA. Usually difficult to recycle.",
        "1. Check for \"Compostable\" labels (PLA).\n" +
                "2. Return large jugs to suppliers for reuse."
    )
)
private fun ImageProxy.toBitmap(): android.graphics.Bitmap? {
    val nv21 = yuv420888ToNv21(this)
    val yuvImage = android.graphics.YuvImage(nv21, android.graphics.ImageFormat.NV21, width, height, null)
    val out = java.io.ByteArrayOutputStream()
    yuvImage.compressToJpeg(android.graphics.Rect(0, 0, width, height), 100, out)
    val imageBytes = out.toByteArray()
    return android.graphics.BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
}

private fun yuv420888ToNv21(image: ImageProxy): ByteArray {
    val planes = image.planes
    val yBuffer = planes[0].buffer
    val uBuffer = planes[1].buffer
    val vBuffer = planes[2].buffer

    val ySize = yBuffer.remaining()
    val uSize = uBuffer.remaining()
    val vSize = vBuffer.remaining()

    val nv21 = ByteArray(ySize + uSize + vSize)
    yBuffer.get(nv21, 0, ySize)
    vBuffer.get(nv21, ySize, vSize)
    uBuffer.get(nv21, ySize + vSize, uSize)

    return nv21
}

class PlasticScannerAnalyzer(private val onResult: (String) -> Unit) : ImageAnalysis.Analyzer {
    private val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

    private val aliasMap = mapOf(
        "1" to listOf("1", "I", "i", "|", "l"),
        "2" to listOf("2", "Z", "z"),
        "5" to listOf("5", "S", "s"),
        "6" to listOf("6", "G", "b"),
        "7" to listOf("7", "T")
    )

    @SuppressLint("UnsafeOptInUsageError")
    override fun analyze(imageProxy: ImageProxy) {
        val bitmap = imageProxy.toBitmap()
        if (bitmap == null) {
            imageProxy.close()
            return
        }

        val cropSize = 400
        val startX = (bitmap.width - cropSize) / 2
        val startY = (bitmap.height - cropSize) / 2
        val safeX = if (startX > 0) startX else 0
        val safeY = if (startY > 0) startY else 0

        val sourceBitmap = Bitmap.createBitmap(bitmap, safeX, safeY, cropSize, cropSize)
        val enhancedBitmap = sourceBitmap.copy(Bitmap.Config.ARGB_8888, true)

        val canvas = android.graphics.Canvas(enhancedBitmap)
        val paint = android.graphics.Paint()
        val colorMatrix = android.graphics.ColorMatrix().apply {
            setSaturation(0f)
            val contrast = 2.0f
            val brightness = -40f
            set(floatArrayOf(
                contrast, 0f, 0f, 0f, brightness,
                0f, contrast, 0f, 0f, brightness,
                0f, 0f, contrast, 0f, brightness,
                0f, 0f, 0f, 1f, 0f
            ))
        }
        paint.colorFilter = android.graphics.ColorMatrixColorFilter(colorMatrix)
        canvas.drawBitmap(enhancedBitmap, 0f, 0f, paint)

        val image = InputImage.fromBitmap(enhancedBitmap, 0)

        recognizer.process(image)
            .addOnSuccessListener { visionText ->
                for (block in visionText.textBlocks) {
                    val text = block.text.replace("\\s".toRegex(), "").uppercase()

                    val materialMap = mapOf(
                        "PET" to "1", "PETE" to "1",
                        "HDPE" to "2", "PEHD" to "2",
                        "PVC" to "3",
                        "LDPE" to "4", "PELD" to "4",
                        "PP" to "5",
                        "PS" to "6",
                        "OTHER" to "7"
                    )

                    for ((key, value) in materialMap) {
                        if (text.contains(key)) {
                            onResult(value) // 抓到 PP 就直接回傳 "5"
                            return@addOnSuccessListener
                        }
                    }

                    val regex = Regex("[1-7]")
                    val match = regex.find(text)

                    if (match != null && text.length <= 4) {
                        onResult(match.value)
                        return@addOnSuccessListener
                    }
                }
            }
            .addOnCompleteListener {
                sourceBitmap.recycle()
                enhancedBitmap.recycle()
                imageProxy.close()
            }
    }
}


