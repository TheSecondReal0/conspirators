package com.csci448.bam.conspirators.ui.home

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.csci448.bam.conspirators.ui.sharedComponents.BoardCard
import com.csci448.bam.conspirators.viewmodel.ConspiratorsViewModel

@Composable
fun HomeScreen(modifier: Modifier, conspiratorsViewModel: ConspiratorsViewModel) {
    val sampleList: List<String> = listOf("How Danny Devito Caused the 2008 Financial Crisis", " uh oh, stinkyyy", "Example 3")
    LazyVerticalGrid (modifier = Modifier.fillMaxSize(), columns = GridCells.Fixed(2))
    {
        items(sampleList) { item ->
            BoardCard(title = item, onClick = {})
        }
    }
}

@Preview
@Composable
fun PreviewHomeScreen() {
    HomeScreen(modifier = Modifier, conspiratorsViewModel = ConspiratorsViewModel())
}