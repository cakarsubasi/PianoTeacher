package com.kocuni.pianoteacher.ui.music

import android.graphics.Paint
import android.graphics.Typeface
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope

import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kocuni.pianoteacher.R
import com.kocuni.pianoteacher.ui.music.PianoKeyMaps.blacks
import com.kocuni.pianoteacher.ui.music.PianoKeyMaps.blacksPos
import com.kocuni.pianoteacher.ui.music.PianoKeyMaps.whites
import com.kocuni.pianoteacher.ui.music.PianoKeyMaps.whitesPos


/**
 * TODO: Should take three resources
 * 1. Next note to be played
 * 2. Note to be played after that
 * 3. Last note actually played
 */
@Preview(showBackground = true)
@Composable
fun Piano(
    // TODO
    blocks: List<Block> = listOf(NoteBlock("C4"), NoteBlock("D4#"), NoteBlock("E4"))
) {

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
            color = Color.Black.toArgb()
            typeface = Typeface.MONOSPACE
        }
        // draw the Piano
        with(painter) { draw( size = size) }

        // White key labels

        for (i in whites.keys) {
            val note = NoteBlock(whites[i] ?: "C0")
            drawNote(this, paint,
                xOffset = 0.5f,
                yOffset = 0.73f,
                parityOffset = 0.015f,
                notePos = i,
                note = note,
                backgroundSize = 1.2f
            )
        }

        // Black key labels
        for (i in blacks.keys) {
            val note = NoteBlock(blacks[i] ?: "C0")
            drawNote(this, paint,
                xOffset = 1f,
                yOffset = 0.10f,
                parityOffset = 0.05f,
                notePos = i,
                note = note,
                backgroundSize = 1.5f)
        }

        for (i in blocks.size-1 downTo 0) {
            val color = when (i) {
                0 -> Block.Colors.Played
                1 -> Block.Colors.First
                2 -> Block.Colors.Second
                else -> Block.Colors.Third
            }
            val text = when (i) {
                0 -> "P"
                else -> i.toString()
            }
            drawBarHelper(
                scope = this,
                paint = paint,
                noteBlock = blocks[i],
                color = color,
                text = text)
        }

    }
}

private fun drawBarHelper(scope: DrawScope,
                          paint: Paint,
                          noteBlock: Block,
                          color: Color,
                          text: String) {
    val isBlack = { block: Block ->
        (block.name.contains("#"))
    }

    val whitePos = { block: Block ->
        whitesPos[block.name] ?: -1
    }

    val blackPos = { block: Block ->
        blacksPos[block.name] ?: -1
    }
    val pos: Int
    val xOffset: Float
    val yOffset: Float
    if (isBlack(noteBlock)) {
        pos = blackPos(noteBlock)
        xOffset = 0.5f
        yOffset = 0.3f
    } else {
        pos = whitePos(noteBlock)
        xOffset = 0.0f
        yOffset = 0.8f
    }
    drawBar(
        scope = scope,
        paint = paint,
        notePos = pos,
        xOffset = xOffset,
        yOffset = yOffset,
        rectColor = color,
        text = text
    )
}


/**
 * Draws bars for played, and upcoming notes
 */
fun drawBar(scope: DrawScope,
            paint: Paint,
            yOffset: Float = 0.8f,
            xOffset: Float = 0.0f,
            notePos: Int,
            rectColor: Color = Block.Colors.Played,
            text: String = "P"
) {
    with(scope) {
        val canvasWidth = this.size.width
        val canvasHeight = this.size.height
        drawRoundRect(
            color = rectColor,
            topLeft = Offset(
                x = (canvasWidth * (notePos + xOffset)) / 22,
                y = yOffset * canvasHeight,
            ),
            cornerRadius = CornerRadius(30f, 30f),
            size = Size(canvasWidth / (22f + 0.5f), canvasHeight / 3),
        )

        drawContext.canvas.nativeCanvas.drawText(
            text,
            canvasWidth*((notePos + xOffset) + 0.5f) / 22,
            canvasHeight * (yOffset + 0.1f),
            paint
        )
    }
}


/**
 * Draws note labels on the piano
 */
fun drawNote(scope: DrawScope,
             paint: Paint,
             yOffset: Float = 0.7f,
             xOffset: Float = 0.5f,
             parityOffset: Float = 0f,
             notePos: Int = 0,
             note: Block,
             backgroundSize: Float = 1f) {
    with(scope) {
        val canvasWidth = this.size.width
        val canvasHeight = this.size.height
        drawCircle(
            color = note.color,
            center = Offset(
                x = canvasWidth*((notePos) + xOffset) / 22,
                y = canvasHeight *
                        if (notePos % 2 == 0) (yOffset+parityOffset)
                        else (yOffset - parityOffset)),
            radius = canvasHeight * backgroundSize / 30,
        )

        drawContext.canvas.nativeCanvas.drawText(
            note.name,
            canvasWidth*((notePos) + xOffset) / 22,
            canvasHeight *
                    if (notePos % 2 == 0) (yOffset+0.02f + parityOffset)
                    else (yOffset+0.02f - parityOffset),
            paint
        )

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
