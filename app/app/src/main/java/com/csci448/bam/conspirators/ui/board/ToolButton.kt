package com.csci448.bam.conspirators.ui.board

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Create
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun ToolButton(onClick: () -> Unit, icon: ImageVector, modifier: Modifier = Modifier, icon2: ImageVector? = null, iconDesc: String, useIcon2: Boolean = false ) {
    IconButton(
        onClick = onClick,
        modifier = modifier
        )
    {
        if (icon2 != null && useIcon2) {
            Icon(icon2, iconDesc)
        }
        else {
            Icon(icon, iconDesc)
        }
    }
}

@Preview
@Composable
fun PreviewToolButton() {
    ToolButton(onClick = {}, icon = Icons.Filled.Create, iconDesc = "ah")
}

