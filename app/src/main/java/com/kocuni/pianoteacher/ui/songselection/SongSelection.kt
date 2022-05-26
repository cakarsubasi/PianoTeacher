package com.kocuni.pianoteacher.ui.songselection

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kocuni.pianoteacher.SongTutorViewModel
import com.kocuni.pianoteacher.utils.data.SongFile


@Composable
fun SongSelection(
    viewModel: SongTutorViewModel,
    returnToTutor: () -> Unit = {},
) {
    val deletionDialog = remember { mutableStateOf(false) }
    val delete_this = remember {mutableStateOf({})}
    val fileManager = viewModel.fileManager
    fileManager.readAllFiles()

    val setSong : (SongFile) -> Unit = {
        viewModel.setSong(it.getTutorable())
        viewModel.setName(it.name)
        returnToTutor()
    }
    val deleteSong : (SongFile) -> Unit = {
        deletionDialog.value = true
        delete_this.value = {
            fileManager.deleteFile(it)
            }
    }


    DeleteDialog(deletionDialog, delete_this)

    Column() {
        TopAppBar() {
            Text(text = "Song Select",
            style = MaterialTheme.typography.h4
            )
        }
        LazyColumn {

            fileManager.songs.forEach {
                item {
                    SongRow(
                        song = SongHolder(it.name),
                        selectSong = { setSong(it) },
                        deleteSong = { deleteSong(it) }
                    )

                }

                item {
                    Spacer(
                        modifier = Modifier.height(10.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun DeleteDialog(
    openDialog: MutableState<Boolean> = remember { mutableStateOf(false) },
    onAccept: MutableState<() -> Unit> = remember { mutableStateOf({})}
) {
    Column() {
        if (openDialog.value) { AlertDialog(
            onDismissRequest = {
                openDialog.value = false
            }, title =  {
                Text(text = "Deletion dialog")
            }, text = {
                Text(text = "Are you sure?")
            }, confirmButton = {
                Button(onClick = {
                    openDialog.value = false
                    onAccept.value()
                }) {
                    Text("Confirm")
                }
            }, dismissButton = {
                Button(onClick = {
                    openDialog.value = false
                }) {
                    Text("Reject")
                }
            }) }
    }
}

@Preview
@Composable
fun SongRow(
    song: SongHolder = SongHolder(),
    selectSong: () -> Unit = {},
    deleteSong: () -> Unit = {},
) {
    Row(
        modifier = Modifier.clickable(onClick = selectSong),
    )
    {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .height(40.dp)
                .padding(horizontal = 4.dp, vertical = 0.dp),
            shape = MaterialTheme.shapes.medium,
            elevation = 4.dp) {
            Text( text = song.name,
                style = MaterialTheme.typography.h5,

            )
        }
        IconButton(onClick = deleteSong
        ) {
            Icon(
                Icons.Filled.Delete, contentDescription = null
            )
        }
    }
    Spacer(modifier = Modifier.height(4.dp))
}

@Composable
fun SongList(
    title: String = "Song List",
    songs: List<SongHolder> = listOf(SongHolder()),
) {
    Column() {
        Text(text = title)
        Column() {
            songs.forEach { song ->
                SongRow(song)
            }
        }
    }
}



@Preview(showBackground = true, showSystemUi = true)
@Composable
fun SongListPreview() {
    val song1 = SongHolder("Hardcoded song 1")
    val song2 = SongHolder("Hardcoded song 2")
    val song3 = SongHolder("Raw song 1")
    val song4 = SongHolder("Raw song 2")
    val song5 = SongHolder("Raw song 3")
    val song6 = SongHolder("Bach 1")
    val song7 = SongHolder("Bach 2")
    val song8 = SongHolder("Bach 3")

    val list1 = listOf(song1, song2)
    val list2 = listOf(song3, song4, song5)
    val list3 = listOf(song6, song7, song8)

    LazyColumn() {
        item {
            SongList("Hardcoded Songs", list1)
        }
        item {
            SongList("Raw Songs", list2)
        }
        item {
            SongList("Inferred Songs", list3)
        }
    }
}

class SongHolder(var name: String = "Test") {

}