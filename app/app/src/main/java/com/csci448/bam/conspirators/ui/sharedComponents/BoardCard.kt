package com.csci448.bam.conspirators.ui.sharedComponents

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.csci448.bam.conspirators.R
import java.util.UUID

@Composable
fun BoardCard(title: String, image: Int? = null, onClick: () -> Unit, userName: String) {
    val cardModifier = Modifier
        .size(200.dp)
        .clickable(onClick = onClick)
        .padding(4.dp)
        .clip(RoundedCornerShape(10.dp))
    if (image != null) {
        Box(modifier = cardModifier,
            contentAlignment = Alignment.Center)
        {
            Image(painterResource(R.drawable.sample_image),
                title,
                Modifier.fillMaxSize().align(Alignment.Center),
                contentScale = ContentScale.Crop)
            Row(Modifier.clip(RoundedCornerShape(topEnd = 20.dp)).background(Color(red = 255, blue = 255, green = 255, alpha = 230))
                .align(Alignment.BottomStart)) {
                Column {
                    Text(title,modifier = Modifier.padding(top = 10.dp, start = 10.dp, end = 10.dp),
                        textAlign = TextAlign.Start,
                        overflow = TextOverflow.Ellipsis)

                    Text(text = userName, modifier = Modifier.padding(start = 15.dp, bottom = 4.dp),
                        textAlign = TextAlign.Center,
                        overflow = TextOverflow.Ellipsis,
                        fontSize = 10.sp)
                }
            }
        }
    }
    else {
        Box(modifier = cardModifier.background(Color.LightGray),contentAlignment = Alignment.Center) {
            Text(title, modifier = Modifier.padding(5.dp),
                textAlign = TextAlign.Center,
                overflow = TextOverflow.Ellipsis)

            Text(text = userName, modifier = Modifier.padding(start = 15.dp, bottom = 4.dp).align(Alignment.BottomStart),
                textAlign = TextAlign.Center,
                overflow = TextOverflow.Ellipsis,
                fontSize = 10.sp)
        }
    }

}

@Preview
@Composable
fun PreviewBoardCard() {
    Row(modifier = Modifier.fillMaxSize()) {
        BoardCard("Avocado", onClick = {}, userName = "poggers")
        BoardCard("Avocado", onClick = {}, image = R.drawable.sample_image, userName = "harry")
    }
}
