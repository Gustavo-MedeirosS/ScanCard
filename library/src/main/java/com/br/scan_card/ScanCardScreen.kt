package com.br.scan_card

import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.github.gustavomedeiros.scancard.R

@Composable
fun ScanCardScreen(
    isFlashlightEnabled: Boolean,
    onFlashlightClick: (Boolean) -> Unit,
    onPreviewViewReady: (PreviewView) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        Text(
            text = stringResource(id = R.string.lbl_position_your_card),
            style = TextStyle(
                fontSize = 20.sp,
                color = Color.White,
                textAlign = TextAlign.Center
            )
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(240.dp)
                .clip(RoundedCornerShape(8.dp))
                .border(
                    color = Color(0xFF53E952),
                    width = 4.dp,
                    shape = RoundedCornerShape(size = 8.dp)
                )
        ) {
            CameraContent(onPreviewViewReady = onPreviewViewReady)
        }

        IconButton(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(Color.White),
            onClick = { onFlashlightClick(!isFlashlightEnabled) },
        ) {
            Icon(
                painter = painterResource(
                    id = if (isFlashlightEnabled) R.drawable.flashlight_off
                    else R.drawable.flashlight_on
                ),
                contentDescription = "Flashlight",
                tint = Color.Black
            )
        }
    }
}

@Composable
fun CameraContent(onPreviewViewReady: (PreviewView) -> Unit) {
    AndroidView(
        modifier = Modifier.fillMaxSize(),
        factory = { context ->
            PreviewView(context).apply {
                layoutParams = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                implementationMode = PreviewView.ImplementationMode.COMPATIBLE
                scaleType = PreviewView.ScaleType.FILL_CENTER
            }.also { previewView ->
                onPreviewViewReady(previewView)
            }
        }
    )
}

@Preview
@Composable
private fun ScanCardScreenPreview() {
    ScanCardScreen(false, {}, {})
}