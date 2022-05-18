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
import androidx.compose.material.MaterialTheme
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
import com.kocuni.pianoteacher.music.data.MidiTable
import com.kocuni.pianoteacher.ui.theme.PianoTeacherTheme


/**
 * TODO: Should take three resources
 * 1. Next note to be played
 * 2. Note to be played after that
 * 3. Last note actually played
 */
@Preview(showBackground = true)
@Composable
fun Piano() {

    val vector = ImageVector.vectorResource(id = R.drawable.ic_piano43key)
    val painter = rememberVectorPainter(image = vector)
    Canvas(modifier = Modifier
        .height(200.dp)
        .fillMaxWidth()) {
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

        // White key labels
        for (i in PianoKeyMaps.whites.keys) {
            drawCircle(
                color = Color.Blue,
                center = Offset(x = canvasWidth*(i+0.5f) / 22, y = canvasHeight * 0.7f),
                radius = size.minDimension / 30,
            )

            drawContext.canvas.nativeCanvas.drawText(
                PianoKeyMaps.whites[i] ?: "C0",
                canvasWidth*(i+0.5f) / 22,
                canvasHeight*0.72f,
                paint
            )
        }


        // Black key labels
        for (i in PianoKeyMaps.whites.keys) {
            drawCircle(
                color = Color.Blue,
                center = Offset(x = canvasWidth*(i+1f) / 22, y = canvasHeight * 0.5f),
                radius = size.minDimension / 30,
            )

            drawContext.canvas.nativeCanvas.drawText(
                PianoKeyMaps.blacks[i] ?: "C0",
                canvasWidth*(i+1f) / 22,
                canvasHeight*0.52f,
                paint
            )
        }

        // played note

        // note to be played

        // note to be played after that
    }
}

object PianoKeyMaps {
    val whites: Map<Int, String> = HashMap<Int,String>().also {
        it[0] = "C3"
        it[1] = "D3"
        it[2] = "E3"
        it[3] = "F3"
        it[4] = "G3"
        it[5] = "A3"
        it[6] = "B3"
        it[7] = "C4"
        it[8] = "D4"
        it[9] = "E4"
        it[10] = "F4"
        it[11] = "G4"
        it[12] = "A4"
        it[13] = "B4"
        it[14] = "C5"
        it[15] = "D5"
        it[16] = "E5"
        it[17] = "F5"
        it[18] = "G5"
        it[19] = "A5"
        it[20] = "B5"
        it[21] = "C6"
    }

    // inverting maps probably has better performance
    val whitesPos = mutableMapOf<String, Int>().also {
        whites.forEach { (k, v) -> it[v] = k }
    }

    // use the keys here to skip drawing unnecessary parts
    val blacks: Map<Int, String> = HashMap<Int, String>().also {
        it[0] = "C3#"
        it[1] = "D3#"

        it[3] = "F3#"
        it[4] = "G3#"
        it[5] = "A3#"

        it[7] = "C4#"
        it[8] = "D4#"

        it[10] = "F4#"
        it[11] = "G4#"
        it[12] = "A4#"

        it[14] = "C5#"
        it[15] = "D5#"

        it[17] = "F5#"
        it[18] = "G5#"
        it[19] = "A5#"

    }

    // inverting maps probably has better performance
    val blacksPos = mutableMapOf<String, Int>().also {
        blacks.forEach { (k, v) -> it[v] = k }
    }

}

class Colors {
    val C = Color(0xFF8080FF)
    val D = Color(0xFFBF80FF)
    val E = Color(0xFFFF80FF)
    val F = Color(0xFFFF80BF)
    val G = Color(0xFFFF8080)
    val A = Color(0xFFFFBF80)
    val B = Color(0xFFFFFF80)
}
