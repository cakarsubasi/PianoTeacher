package com.kocuni.pianoteacher.ui.music

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kocuni.pianoteacher.Greeting
import com.kocuni.pianoteacher.SongTutorViewModel
import com.kocuni.pianoteacher.TutorControls
import com.kocuni.pianoteacher.music.SongTutor
import com.kocuni.pianoteacher.music.data.SampleSongs
import com.kocuni.pianoteacher.ui.theme.PianoTeacherTheme

@Preview(showSystemUi = true, showBackground = true)
@Composable
fun DefaultPreview3() {
    PianoTeacherTheme {
        Greeting("Android")
        Column {
            Row() {
                TopAppBar() {
                    Row (
                        modifier = Modifier.align(Alignment.CenterVertically).fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                            ) {
                        Text(text = "Sample Song",
                        modifier = Modifier.align(Alignment.CenterVertically))
                        Button( onClick = {},
                            ) {
                            Text("Change Song")
                        }

                    }



                }
            }
            Card(
                modifier = Modifier.padding(4.dp),
                elevation = 10.dp,
            ) {
                NoteList()
            }
            Card {
                Note()
            }
            Card {
                Piano()
            }
            Card {
                TutorControls()
            }
        }

    }
}


@Composable
fun Tutor(
    viewModel: SongTutorViewModel,
    song_select: () -> Unit = {},
) {
    var uiState = viewModel.uiState
    LaunchedEffect(viewModel.uiState) {
        uiState = viewModel.uiState
    }
    val status: String =
        when (uiState.status) {
            SongTutor.STATE.IDLE -> "idle"
            SongTutor.STATE.FALSE -> "false"
            else -> "true" }
    Column() {
        // top menu bar
        TopAppBar(

        ) {

            Text(
                // TODO: get the actual name
                text = viewModel.songNameState.name
            )
            Button(
                // TODO: formatting
                onClick = song_select,
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = Color.Magenta
                )

            ) {
                Text(
                    text = "Change Song"
                )
            }
        }
        Row {

        }
        // note stream
        NoteList(uiState.nextNotes, currentPos = uiState.currentNote)
        // detected note
        Row {
            Note(NoteBlock(uiState.playedNote))
            Text( text = status )
        }

        // piano
        Card() {
            Piano()
        }

        // controls
        Row () {
            TutorControls(controls = viewModel.controls)
        }

    }
}

@Preview(showBackground = true)
@Composable
fun NoteList(
    noteList: List<Block> = SongTutor(stream = SampleSongs.song1()).endToEnd,
    currentPos: Int = 1,
) {
    LazyRow(
        contentPadding = PaddingValues(1.dp)
    ) {
        items(noteList.size) { index ->
            Note(noteList[index], (index == currentPos))
        }
    }
}

@Preview
@Composable
fun Note(note: Block = NoteBlock(), current: Boolean = false) {
    Card(
        modifier = Modifier.size(40.dp).padding(all = 4.dp),
        elevation = 2.dp,
        backgroundColor = if (current) Color.Green else note.color,

        ) {
        Surface(
            modifier = Modifier.padding(all = 4.dp),
            shape= MaterialTheme.shapes.small,
            elevation= 2.dp,
            color = note.color,) {
            Text(
                text = note.name,
                textAlign = TextAlign.Center
            )
        }
    }
}