package com.csci448.busche.testing.components

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

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