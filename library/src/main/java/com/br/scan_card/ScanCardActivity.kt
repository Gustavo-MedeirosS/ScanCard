package com.br.scan_card

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import android.widget.Toast.LENGTH_LONG
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
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.github.gustavomedeiros.scancard.R
import com.google.common.util.concurrent.ListenableFuture
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.TextRecognizer
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.launch

class ScanCardActivity : AppCompatActivity() {

    private val TAG = "ScanCard"

    private lateinit var cameraProviderFuture: ListenableFuture<ProcessCameraProvider>
    private lateinit var camera: Camera
    private lateinit var previewView: PreviewView
    private lateinit var viewModel: ScanCardViewModel
    private lateinit var timeoutRunnable: Runnable
    private val timeoutHandler = Handler(Looper.getMainLooper())
    private var lastAnalyzedTime = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(owner = this)[ScanCardViewModel::class.java]

        setContent {
            ScanCardScreen(
                onFlashlightClick = { onFlashlightClick() },
                onPreviewViewReady = { previewView ->
                    this.previewView = previewView
                    startCamera()
                }
            )
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED
        ) {
            Log.i(TAG, "Camera permission not granted")
            setResult(CAMERA_NOT_GRANTED_CODE)
            Toast.makeText(
                this,
                R.string.lbl_camera_permission_warning,
                LENGTH_LONG
            ).show()
            finish()
        }

        timeoutRunnable = Runnable {
            val cardData = viewModel.cardData.value
            if (cardData != null && cardData.number.isBlank() || cardData!!.validity.isBlank()) {
                Log.i(TAG, "Timeout reached without detecting card.")
                Toast.makeText(
                    this,
                    R.string.lbl_card_not_detected,
                    LENGTH_LONG
                ).show()
                setResult(RESULT_CANCELED)
                finish()
            }
        }

        timeoutHandler.postDelayed(timeoutRunnable, 10_000)
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
            setResult(RESULT_CANCELED)
            Toast.makeText(
                this,
                R.string.lbl_camera_perm_not_granted,
                LENGTH_LONG
            ).show()
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
            it.surfaceProvider = previewView.surfaceProvider
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
                viewModel.analyzeCard(visionText)

                lifecycleScope.launch {
                    viewModel.isCardComplete.collect { isCompleted ->
                        if (isCompleted) {
                            val intent = Intent()
                            intent.putExtra(CREDIT_CARD_DATA, viewModel.cardData.value)

                            setResult(RESULT_OK, intent)
                            finish()
                        }
                    }
                }
            }
            .addOnFailureListener { exception ->
                Log.e(TAG, "Error to process image", exception)
            }
            .addOnCompleteListener {
                imageProxy.close()
            }

    }

    private fun onFlashlightClick() {
        if (camera.cameraInfo.hasFlashUnit()) {
            if (viewModel.isFlashlightEnabled.value) {
                camera.cameraControl.enableTorch(false)
            } else {
                camera.cameraControl.enableTorch(true)
            }
            viewModel.enableFlashlight()
        } else {
            Toast.makeText(
                this,
                R.string.lbl_flashlight_not_available,
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun onPause() {
        super.onPause()
        timeoutHandler.removeCallbacks(timeoutRunnable)
    }

    override fun onResume() {
        super.onResume()
        if (!::timeoutRunnable.isInitialized) {
            timeoutRunnable = Runnable { /* ... */ }
        }
        timeoutHandler.postDelayed(timeoutRunnable, 10_000)
    }

    override fun onDestroy() {
        super.onDestroy()
        timeoutHandler.removeCallbacks(timeoutRunnable)
    }

    companion object {
        const val CAMERA_PERMISSION_CODE = 1001
        const val CAMERA_NOT_GRANTED_CODE = -1

        const val CREDIT_CARD_DATA = "CREDIT_CARD_DATA"
    }
}