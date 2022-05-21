package com.kocuni.pianoteacher.ui.music

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.*
import androidx.compose.runtime.*
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
            Row {
                SongMenuBar()
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

@Preview
@Composable
fun SongMenuBar(
    songName: String = "Sample Name",
    song_select: () -> Unit = {},
) {
    TopAppBar() {
        Row(
            modifier = Modifier
                .align(Alignment.CenterVertically)
                .fillMaxWidth(0.5f),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = songName,
                modifier = Modifier.align(Alignment.CenterVertically),
                textAlign = TextAlign.Left
            )
        }
        Row (
            modifier = Modifier
                .align(Alignment.CenterVertically)
                .fillMaxWidth(1f)
                .wrapContentWidth(Alignment.End),
            horizontalArrangement = Arrangement.End
        ) {
            Button(
                onClick = song_select,
                colors = ButtonDefaults.buttonColors(backgroundColor = Color.Magenta)

            ) {
                Text(
                    text = "Change Song"
                )
            }
        }
    }
}

@Preview
@Composable
fun StreamDropdown() {
    var expanded by remember { mutableStateOf(false) }
    var selectedIndex by remember { mutableStateOf(0) }
    val items = listOf("SOPRANO", "TENOR")
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentWidth(Alignment.End),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = items[selectedIndex],
        modifier = Modifier.clickable { expanded = true })
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            items.forEachIndexed { index, s ->
                DropdownMenuItem(
                    onClick = {
                              expanded = false
                        selectedIndex = index
                    },
                ) {
                    Text( text = s)
                }
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
    Column {
        // top menu bar
        SongMenuBar(
            songName = viewModel.songState.name,
            song_select = song_select,
        )
        // note stream
        NoteList(uiState.nextNotes, currentPos = uiState.currentNote)
        // detected note
        Row {
            Note(NoteBlock(uiState.playedNote))
            Text( text = status )

            StreamDropdown()
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
        modifier = Modifier
            .size(40.dp)
            .padding(all = 4.dp),
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