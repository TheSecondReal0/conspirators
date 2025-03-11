package com.csci448.bam.conspirators.ui.sharedComponents

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import java.util.UUID

@Composable
fun BoardCard(title: String, imageBitmap: ImageBitmap? = null, onClick: () -> Unit) {
    val cardModifier = Modifier
        .size(100.dp)
        .clickable(onClick = onClick)
        .padding(4.dp)
        .clip(RoundedCornerShape(10.dp))
    if (imageBitmap != null) {
        Box(modifier = cardModifier,
            contentAlignment = Alignment.Center)
        {
            Text(title,modifier = Modifier.padding(5.dp),
                textAlign = TextAlign.Center,
                overflow = TextOverflow.Ellipsis)
            Image(imageBitmap,title, Modifier.fillMaxSize().align(Alignment.Center))
        }
    }
    else {
        Box(modifier = cardModifier.background(Color.LightGray),contentAlignment = Alignment.Center) {
            Text(title, modifier = Modifier.padding(5.dp),
                textAlign = TextAlign.Center,
                overflow = TextOverflow.Ellipsis)
        }
    }

}

@Preview
@Composable
fun PreviewBoardCard() {
    BoardCard("Avocado", onClick = {})
}
