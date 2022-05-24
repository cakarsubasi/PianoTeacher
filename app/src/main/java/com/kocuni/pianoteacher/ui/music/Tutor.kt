package com.kocuni.pianoteacher.ui.music

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kocuni.pianoteacher.SongTutorViewModel
import com.kocuni.pianoteacher.music.SongTutor
import com.kocuni.pianoteacher.music.data.SampleSongs
import com.kocuni.pianoteacher.ui.music.data.Block
import com.kocuni.pianoteacher.ui.music.data.NoteBlock
import com.kocuni.pianoteacher.ui.theme.PianoTeacherTheme

@Preview(showSystemUi = true, showBackground = true)
@Composable
fun DefaultPreview3() {
    PianoTeacherTheme {

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

fun makeToast(context: Context,
              message: String = "Sample Text") {
    Toast.makeText(context, message, Toast.LENGTH_LONG).show()
}


@Composable
fun Tutor(
    viewModel: SongTutorViewModel,
    song_select: () -> Unit = {},
) {
    val mContext = LocalContext.current

    var uiState = viewModel.uiState
    LaunchedEffect(viewModel.uiState) {
        uiState = viewModel.uiState

    }

    val status: String =
        when (uiState.status) {
            SongTutor.STATE.IDLE -> "idle"
            SongTutor.STATE.FALSE -> "false"
            else -> "true" }

    if (uiState.status == SongTutor.STATE.CORRECT &&
            uiState.correctCount % 5 == 0 &&
            uiState.autoAdvance) {
        makeToast(mContext, "${uiState.correctCount} Correct!")
    }

    LazyColumn(
        modifier = Modifier.fillMaxHeight(),
        verticalArrangement = Arrangement.SpaceBetween

    ) {
        item() {
            Column(

            ) {
                // top menu bar
                SongMenuBar(
                    songName = viewModel.songState.name,
                    song_select = song_select,
                )
                // note stream
                NoteList(uiState.nextNotesWithMeasures, currentPos = uiState.currentNoteIndex)
                // detected note
                Row {
                    Note(uiState.playedNote)
                    Text(
                        modifier = Modifier.align(Alignment.CenterVertically),
                        text = status
                    )

                    StreamDropdown(
                        items = viewModel.getVoices(),
                        voice_select = viewModel.setVoice
                    )
                }
                // piano
                Card() {
                    Piano(
                        uiState.nextNotesWithoutMeasures
                    )
                }
            }
        }
        item {
            Column(
                verticalArrangement = Arrangement.Bottom
            ) {
                Row (
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                    TutorControls(controls = viewModel.controls,
                        midiState = viewModel.midiState.midiEnabled)
                }
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
fun StreamDropdown(
    items: List<String> = listOf("SOPRANO", "TENOR"),
    voice_select: (String) -> Unit = {}
) {
    var expanded by remember { mutableStateOf(false) }
    var selectedIndex by remember { mutableStateOf(0) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentWidth(Alignment.End)
            .padding(4.dp),
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
                        voice_select(s)
                    },
                ) {
                    Card {
                        if (index == selectedIndex) {
                            Icon(Icons.Rounded.Check, contentDescription = null)
                        }
                    }
                    Text( text = s)
                }
            }
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
            .size(width = 60.dp, height = 60.dp)
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
                modifier = Modifier.fillMaxHeight(),
                text = note.name,
                textAlign = TextAlign.Center,
            )
        }
    }
}