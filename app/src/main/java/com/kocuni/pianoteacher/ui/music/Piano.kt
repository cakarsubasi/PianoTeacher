package com.kocuni.pianoteacher.ui.music

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.graphics.drawable.ShapeDrawable
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.constraintlayout.widget.ConstraintLayout
import com.kocuni.pianoteacher.R

@Preview(showBackground = true)
@Composable
fun Piano() {
    LocalContext.current.assets

    val vector = ImageVector.vectorResource(id = R.drawable.ic_piano88key)
    val painter = rememberVectorPainter(image = vector)
    Canvas(modifier = Modifier.fillMaxSize().fillMaxHeight(0.5f)) {
        val canvasWidth = size.width
        val canvasHeight = size.height
        val size = Size(canvasWidth, canvasHeight/3.0f)

        with(painter) {
            draw( size = size,
            )

        }
        drawCircle(
            color = Color.Blue,
            center = Offset(x = canvasWidth / 12, y = canvasHeight / 6),
            radius = size.minDimension / 40
        )

    }
}

@Composable
fun PianoBase() {
    Icon(
        painter = painterResource(id = R.drawable.ic_piano88key,),
        contentDescription = null,
        tint = Color.Unspecified
    )
}
