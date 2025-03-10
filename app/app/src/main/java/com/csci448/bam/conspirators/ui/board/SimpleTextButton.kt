package com.csci448.bam.conspirators.ui.board

import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun SimpleTextButton(
    onClick: () -> Unit,
    label: String,
    modifier: Modifier = Modifier
) {
    TextButton (
        onClick = onClick,
        modifier = modifier
    ) {
        Text(label)
    }
}