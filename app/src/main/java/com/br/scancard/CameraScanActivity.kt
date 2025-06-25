package com.br.scancard

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.annotation.OptIn
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.br.scancard.databinding.ActivityCameraScanBinding
import com.google.common.util.concurrent.ListenableFuture
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.TextRecognizer
import com.google.mlkit.vision.text.latin.TextRecognizerOptions

class CameraScanActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCameraScanBinding
    private lateinit var cameraProviderFuture: ListenableFuture<ProcessCameraProvider>
    private lateinit var camera: Camera
    private lateinit var flashlightImageButton: ImageButton

    private val timeoutHandler = Handler(Looper.getMainLooper())
    private lateinit var timeoutRunnable: Runnable

    private val CAMERA_PERMISSION_CODE = 1001

    private var lastAnalyzedTime = 0L
    private var isFlashlightEnabled: Boolean = false
    private var isCardNumberValid: Boolean = false

    private var numberCard: String? = null
    private var validityCard: String? = null
    private var cvvCard: String? = null
    private var flagCard: String? = null
    private var cvvLength: Int? = null
    private var numberMaxLength: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCameraScanBinding.inflate(layoutInflater)
//        setContentView(binding.root)
        setContent {
            ScanCardScreen()
        }

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED
        ) {
            Log.d("CardScan", "camera permission not granted")
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.CAMERA),
                CAMERA_PERMISSION_CODE
            )
        } else {
            Log.d("CardScan", "camera permission granted")
            startCamera()
        }

        timeoutRunnable = Runnable {
            if (numberCard.isNullOrBlank() || validityCard.isNullOrBlank()) {
                Log.d("CardScan", "Timeout reached without detecting card.")
                Toast.makeText(
                    this,
                    "Cartão não detectado. Por favor, tente novamente.",
                    Toast.LENGTH_LONG
                ).show()
                setResult(RESULT_CANCELED)
                finish()
            }
        }
        timeoutHandler.postDelayed(timeoutRunnable, 10_000)

//        flashlightImageButton = binding.ibFlashlight
        flashlightImageButton.setOnClickListener { onFlashlightClick() }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_PERMISSION_CODE && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startCamera()
        } else {
            setResult(Activity.RESULT_CANCELED)
            Toast.makeText(this, "Camera permission is not granted", Toast.LENGTH_LONG).show()
            finish()
        }
    }

    private fun startCamera() {
        cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            bindPreview(cameraProvider)
        }, ContextCompat.getMainExecutor(this))
    }

    private fun bindPreview(cameraProvider: ProcessCameraProvider) {
        val preview = Preview.Builder().build().also {
//            it.setSurfaceProvider(binding.previewView.surfaceProvider)
        }

        val imageAnalysis = ImageAnalysis.Builder()
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build()

        imageAnalysis.setAnalyzer(ContextCompat.getMainExecutor(this)) { imageProxy ->
            val currentTime = System.currentTimeMillis()
            if (currentTime - lastAnalyzedTime >= 1000L) {
                lastAnalyzedTime = currentTime
                processImageProxy(imageProxy)
            } else {
                imageProxy.close()
            }
        }

        val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
        camera = cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageAnalysis)
    }

    @OptIn(ExperimentalGetImage::class)
    private fun processImageProxy(imageProxy: ImageProxy) {

        val mediaImage = imageProxy.image

        if (mediaImage == null) {
            imageProxy.close()
            return
        }

        val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
        val recognizer: TextRecognizer =
            TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

        recognizer.process(image)
            .addOnSuccessListener { visionText ->
                analyzeCard(visionText)
                if (
                    !numberCard.isNullOrBlank()
                    && !validityCard.isNullOrBlank()
                    && isCardNumberValid
                ) {
                    val intent = Intent()
                    intent.putExtra(CARD_NUMBER, numberCard)
                    intent.putExtra(CARD_NUMBER_MAX_LENGTH, numberMaxLength)
                    intent.putExtra(CARD_VALIDITY, validityCard)
                    intent.putExtra(CARD_CVV, cvvCard ?: "")
                    intent.putExtra(CARD_CVV_LENGTH, cvvLength)
                    intent.putExtra(CARD_FLAG, flagCard)

                    timeoutHandler.removeCallbacks(timeoutRunnable)
                    setResult(RESULT_OK, intent)
                    imageProxy.close()
                    finish()
                }
            }
            .addOnFailureListener { exception ->
                Log.e("CardScan", "Error to process image", exception)
            }
            .addOnCompleteListener {
                imageProxy.close()
            }

    }

    private fun analyzeCard(text: Text) {
        val lines = text.textBlocks.flatMap { it.lines }

        lines.forEach { line ->
            val lineText = line.text

            // Card Number (13-16 digits)
            if (
                !isCardNumberValid
                && numberCard.isNullOrBlank()
                && Regex("""\b(?:\d[ -]*?){13,16}\b""").matches(lineText)
            ) {
                Log.d("CardScan", "Number card detected: $lineText")
                numberCard = lineText
                val cleanedCardNumber = lineText.replace(" ", "").trim()
                flagCard = CreditCardHelper.getCardFlag(number = cleanedCardNumber)
                isCardNumberValid = CreditCardHelper.isCardNumberValid(number = cleanedCardNumber)
                Log.d("CardScan", "Is card number valid: $isCardNumberValid")
            }

            // Validity (12/24, 12-24, 12.24)
            if (validityCard.isNullOrBlank() && Regex("""\d{2}[/\-.]\d{2}""").matches(lineText)) {
                Log.d("CardScan", "Validity card detected: $lineText")
                validityCard = lineText
            }

            // CVV (3 or 4 digits)
            val match = Regex("""(?i)(CVV|CVC)[^\d]{0,5}(\d{3,4})""").find(lineText)
            if (match != null) {
                Log.d("CardScan", "CVV card detected: $lineText")
                cvvCard = match.groupValues[2] // captura apenas os dígitos
            } else if (Regex("""^\d{3,4}$""").matches(lineText)) {
                Log.d("CardScan", "CVV card detected: $lineText")
                cvvCard = lineText // trata caso onde o CVV está isolado
            }
        }
    }

    private fun onFlashlightClick() {
        if (camera.cameraInfo.hasFlashUnit()) {
            if (isFlashlightEnabled) {
                isFlashlightEnabled = false
//                flashlightImageButton.setBackgroundResource(R.drawable.flashlight_button_bg_disabled)
//                flashlightImageButton.setImageResource(R.drawable.flashlight_off)
                camera.cameraControl.enableTorch(false)
            } else {
                isFlashlightEnabled = true
//                flashlightImageButton.setBackgroundResource(R.drawable.flashlight_button_bg_enabled)
//                flashlightImageButton.setImageResource(R.drawable.flashlight_on)
                camera.cameraControl.enableTorch(true)
            }
        } else {
            Toast.makeText(this, "A Lanterna não está disponível", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        timeoutHandler.removeCallbacks(timeoutRunnable)
    }

    companion object {
        const val CARD_NUMBER = "CARD_NUMBER"
        const val CARD_NUMBER_MAX_LENGTH = "CARD_NUMBER_MAX_LENGTH"
        const val CARD_VALIDITY = "CARD_VALIDITY"
        const val CARD_CVV = "CARD_CVV"
        const val CARD_CVV_LENGTH = "CARD_CVV_LENGTH"
        const val CARD_FLAG = "CARD_FLAG"
    }
}