package com.csci448.bam.conspirators.ui.home

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column


import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign

import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import com.csci448.bam.conspirators.R
import com.csci448.bam.conspirators.data.firestore.Board
import com.csci448.bam.conspirators.ui.sharedComponents.BoardCard
import com.csci448.bam.conspirators.ui.sharedComponents.BoardCardExpanded
import com.csci448.bam.conspirators.viewmodel.ConspiratorsViewModel

@Composable
fun HomeScreen(modifier: Modifier, conspiratorsViewModel: ConspiratorsViewModel, editClicked: (Board) -> Unit) {
    var displayExpandedView by remember { mutableStateOf(false) }
    var boardToView: Board? by remember { mutableStateOf(null) }
    DisposableEffect(conspiratorsViewModel) {
        conspiratorsViewModel.addListenerForBoardsWithUserId()
        onDispose { conspiratorsViewModel.removeListenerForBoardsWithUserId() }
    }

    Column(modifier = modifier.fillMaxSize()) {
        Text(
            text = "My Boards",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(vertical = 16.dp),
            textAlign = TextAlign.Left
        )

        LazyVerticalGrid(
            modifier = Modifier.weight(1f),
            columns = GridCells.Fixed(2)
        ) {
            items(conspiratorsViewModel.mBoards.values.toList(), key = { it.id }) { item ->
                BoardCard(
                    title = item.name,
                    image = R.drawable.sample_image,
                    onClick = {
                        displayExpandedView = true
                        boardToView = item
                    },
                    userName = "sample user"
                )
            }
        }
    }

    if (displayExpandedView && boardToView != null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            BoardCardExpanded(
                board = boardToView!!,
                closeClicked = {
                    displayExpandedView = false
                },
                editClicked = {
                    editClicked(boardToView!!)
                    Log.i("HS", "edit clicked")
                }
            )
        }
    }
}