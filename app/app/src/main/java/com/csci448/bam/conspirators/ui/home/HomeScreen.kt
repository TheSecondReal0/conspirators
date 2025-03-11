package com.csci448.bam.conspirators.ui.home

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.csci448.bam.conspirators.R
import com.csci448.bam.conspirators.data.Board
import com.csci448.bam.conspirators.data.User
import com.csci448.bam.conspirators.ui.sharedComponents.BoardCard
import com.csci448.bam.conspirators.viewmodel.ConspiratorsViewModel
import java.util.UUID

@Composable
fun HomeScreen(modifier: Modifier, conspiratorsViewModel: ConspiratorsViewModel) {

    LazyVerticalGrid (modifier = Modifier.fillMaxSize(), columns = GridCells.Fixed(2))
    {
        items(conspiratorsViewModel.boards) { item ->
            BoardCard(title = item.name, image = R.drawable.sample_image, onClick = {}, userName = "sample user")
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
    ))
}