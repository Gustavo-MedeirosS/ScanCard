package com.br.scancard

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View

class ScannerOverlayView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : View(context, attrs) {

    private val borderPaint = Paint().apply {
        color = Color.parseColor("#53E952") // Verde
        style = Paint.Style.STROKE
        strokeWidth = 8f
        isAntiAlias = true
    }

    private val backgroundPaint = Paint().apply {
        color = Color.parseColor("#99000000") // Preto com opacidade
    }

    private val clearPaint = Paint().apply {
        xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
    }

    private val rect = RectF()

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val saveCount = canvas.saveLayer(null, null)

        // Fundo escuro
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), backgroundPaint)

        // Dimensões do centro (área visível da câmera)
        val margin = 64f
        rect.set(margin, margin, width - margin, height - margin)

        // Remove o centro (janela transparente)
        canvas.drawRoundRect(rect, 32f, 32f, clearPaint)

        // Desenha borda verde
        canvas.drawRoundRect(rect, 32f, 32f, borderPaint)

        canvas.restoreToCount(saveCount)
    }
}