package com.csci448.bam.conspirators.ui.profile

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.currentComposer
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.csci448.bam.conspirators.R
import com.csci448.bam.conspirators.ui.sharedComponents.BoardCard
import com.csci448.bam.conspirators.viewmodel.ConspiratorsViewModel

@Composable
fun PostGrid(conspiratorsViewModel: ConspiratorsViewModel) {
    LazyVerticalGrid(
        modifier = Modifier.fillMaxSize().padding(horizontal = 5.dp, vertical = 3.dp),
        columns = GridCells.Fixed(2)
    )
    {
        items(conspiratorsViewModel.myBoards) { item ->
            BoardCard(
                title = item.name,
                imageUrl = item.thumbnailImageUrl,
                onClick = {},
                userName = conspiratorsViewModel.thisUser?.displayName ?: ""
            )
        }
    }
}