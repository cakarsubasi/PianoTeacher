package com.kocuni.pianoteacher.ui.songselection

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import com.kocuni.pianoteacher.SongTutorViewModel
import com.kocuni.pianoteacher.music.data.TutorableSong
import com.kocuni.pianoteacher.utils.data.SongFile


// TODO

@Composable
fun SongSelection(
    viewModel: SongTutorViewModel,
    returnToTutor: () -> Unit = {},
) {
    val songs = viewModel.fileManager.songs
    val setSong : (SongFile) -> Unit = {
        viewModel.setSong(it.getTutorable())
        returnToTutor()
    }
    LazyColumn {
        // TODO
        songs.forEach {
            item {
                SongRow(
                    SongHolder(it.name)
                ) { setSong(it) }
            }
        }
    }
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

@Preview
@Composable
fun SongRow(
    song: SongHolder = SongHolder(),
    onClick: () -> Unit = {},
) {
    Row(
        modifier = Modifier.clickable(onClick = onClick),
        )
    {
        Surface(shape = MaterialTheme.shapes.medium, elevation = 1.dp) {
            Text( text = song.name,
                 style = MaterialTheme.typography.body2,
            )
        }
    }
    Spacer(modifier = Modifier.height(4.dp))
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