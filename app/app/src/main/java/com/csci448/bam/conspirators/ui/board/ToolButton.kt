package com.csci448.bam.conspirators.ui.board

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Create
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun ToolButton(onClick: () -> Unit,
               icon: ImageVector,
               modifier: Modifier = Modifier,
               iconDesc: String,
               isLoading: Boolean = false) {
    IconButton(
        onClick = onClick,
        modifier = modifier,
        enabled = !isLoading
        )
    {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(24.dp),
                strokeWidth = 2.dp
            )
        } else {
            Icon(icon, iconDesc)
        }
    }
}

@Preview
@Composable
fun PreviewToolButton() {
    ToolButton(onClick = {}, icon = Icons.Filled.Create, iconDesc = "ah")
}

