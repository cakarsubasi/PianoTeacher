package com.kocuni.pianoteacher.ui.music

import android.graphics.Bitmap
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.graphics.drawable.ShapeDrawable
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline

import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.widget.ConstraintLayout
import com.kocuni.pianoteacher.R

@Preview(showBackground = true)
@Composable
fun Piano() {
    LocalContext.current.assets

    val vector = ImageVector.vectorResource(id = R.drawable.ic_piano43key)
    val painter = rememberVectorPainter(image = vector)
    Canvas(modifier = Modifier.height(200.dp).fillMaxWidth()) {
        val canvasWidth = size.width
        val canvasHeight = size.height
        val size = Size(canvasWidth, canvasHeight)
        val paint = Paint().apply {
            textAlign = Paint.Align.CENTER
            textSize = 30f
            color = Color.Red.toArgb()
            typeface = Typeface.MONOSPACE
        }

        with(painter) {
            draw( size = size,
            )

        }

        for (i in 0..22) {
            drawCircle(
                color = Color.Blue,
                center = Offset(x = canvasWidth*(i+0.5f) / 22, y = canvasHeight * 0.7f),
                radius = size.minDimension / 30,
            )
            drawContext.canvas.nativeCanvas.drawText(
                "C0",
                canvasWidth*(i+0.5f) / 22,
                canvasHeight*0.7f,
                paint
            )
        }
    }
}

@Composable
fun PianoBase() {
    Image(
        painter = painterResource(id = R.drawable.ic_piano88key,),
        contentDescription = null,

    )
}
