package com.csci448.bam.conspirators.ui.list

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.csci448.bam.conspirators.viewmodel.ConspiratorsViewModel

@Composable
fun ListScreen(modifier: Modifier = Modifier, conspiratorsViewModel: ConspiratorsViewModel) {
    Column(modifier) {
        Text("list screen so cool many wow")
    }
}