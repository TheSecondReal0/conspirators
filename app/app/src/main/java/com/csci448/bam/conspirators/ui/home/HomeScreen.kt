package com.csci448.bam.conspirators.ui.home

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.csci448.bam.conspirators.R
import com.csci448.bam.conspirators.data.Board
import com.csci448.bam.conspirators.data.User
import com.csci448.bam.conspirators.ui.sharedComponents.BoardCard
import com.csci448.bam.conspirators.ui.sharedComponents.BoardCardExpanded
import com.csci448.bam.conspirators.viewmodel.ConspiratorsViewModel
import java.util.UUID

@Composable
fun HomeScreen(modifier: Modifier, conspiratorsViewModel: ConspiratorsViewModel, editClicked: () -> Unit) {
    var displayExpandedView by remember { mutableStateOf(false) }
    var boardToView: Board? by remember { mutableStateOf(null) }
    LazyVerticalGrid (modifier = Modifier.fillMaxSize(), columns = GridCells.Fixed(2))
    {
        items(conspiratorsViewModel.boards) { item ->
            BoardCard(title = item.name, image = R.drawable.sample_image, onClick = {
                displayExpandedView = true
                boardToView = item
            }, userName = "sample user")
        }
    }
    if (displayExpandedView == true && boardToView != null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            BoardCardExpanded(boardToView!!, closeClicked = {
                displayExpandedView = false
            }, editClicked = {editClicked()
            Log.i("HS", "edit clicked")})

        }
    }
}

@Preview
@Composable
fun PreviewHomeScreen() {
    HomeScreen(modifier = Modifier, conspiratorsViewModel = ConspiratorsViewModel(
        boards = listOf(Board(R.drawable.sample_image, "sample_1", UUID.randomUUID()),
                        Board(R.drawable.sample_image, "How Danny Devito Caused the 2008 Financial Crisis", UUID.randomUUID()),
                        Board(R.drawable.sample_image, "uh oh stinky", UUID.randomUUID())),
        users = listOf(User("AAAA", UUID.randomUUID(), R.drawable.sample_image),
            User("AAAA2", UUID.randomUUID(), R.drawable.sample_image),
            User("AAAA3", UUID.randomUUID(), R.drawable.sample_image))
    ),
        editClicked = {})
}