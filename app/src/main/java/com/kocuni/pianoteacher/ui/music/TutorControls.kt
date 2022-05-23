package com.kocuni.pianoteacher.ui.music

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.kocuni.pianoteacher.SongTutorViewModel

@Preview
@Composable
fun TutorControls(
    controls: SongTutorViewModel.LambdaTutorControls = SongTutorViewModel.LambdaTutorControls(),
    midiState: Boolean = false,
) {
    var tutorPushed by remember { mutableStateOf(false) }
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.align(alignment = Alignment.CenterHorizontally)
        ) {
            Button(onClick = { controls.beginning() }) {
                Text("Beginning")
            }
            Button(onClick = { controls.nextMeasure() }) {
                Text("Next Measure")
            }
            Button(onClick = { controls.prevMeasure() }) {
                Text("Prev Measure")
            }

        }
        Row {
            Surface(
                shape = MaterialTheme.shapes.small
            ) {
                val tint by animateColorAsState(
                    if (tutorPushed) Block.Colors.First else MaterialTheme.colors.primary)
                IconToggleButton(
                    modifier = Modifier.background(color= tint),
                    checked = tutorPushed,
                    onCheckedChange = { tutorPushed = it; controls.playSet(it) }) {
                    //Icon(Icons.Filled.PlayArrow, contentDescription = "", tint = tint)
                    Text("Auto Advance")
                }
            }
            Button(onClick = { controls.nextChord() }) {
                Text("Next Chord")
            }
            Button(onClick = { controls.prevChord() }) {
                Text("Prev Chord")
            }
            // Midi
            Surface(
                shape = MaterialTheme.shapes.small
            ) {
                IconToggleButton(
                    modifier = Modifier.background(color= Color.Green),
                    checked = midiState,
                    onCheckedChange = { controls.midiSet(it) }) {
                    val tint by animateColorAsState(
                        if (midiState) Color(0xFFEC407A) else Color(0xFFB0BEC5)
                    )
                    Icon(Icons.Filled.PlayArrow, contentDescription = "", tint = tint)
                }
            }
        }
    }
}