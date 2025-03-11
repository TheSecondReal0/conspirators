package com.csci448.bam.conspirators.ui.sharedComponents

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.twotone.Close
import androidx.compose.material3.Card
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.modifier.modifierLocalOf
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.csci448.bam.conspirators.R
import com.csci448.bam.conspirators.data.AddedComponents
import com.csci448.bam.conspirators.data.AddedComponents.Companion.scale
import com.csci448.bam.conspirators.data.AddedComponents.Companion.screenSize
import com.csci448.bam.conspirators.data.Board
import java.util.UUID

@Composable
fun BoardCardExpanded(board: Board, closeClicked: ()-> Unit, editClicked: ()-> Unit) {
    var boardTitle by rememberSaveable { mutableStateOf(board.name) }

    Box(modifier = Modifier.fillMaxSize(0.8f).clip(RoundedCornerShape(10.dp)), contentAlignment = Alignment.Center) {

        Image(painterResource(R.drawable.sample_image), board.name, modifier = Modifier.fillMaxSize().align(Alignment.Center), contentScale = ContentScale.Crop)
        Row(modifier = Modifier.align(Alignment.TopCenter)) {
            TextField(boardTitle,
                onValueChange = {
                    boardTitle = it
                    board.name = boardTitle
                },
                maxLines = 3
            )
            TextButton(onClick = closeClicked, Modifier.background(Color.DarkGray)) { Text("x", color = Color.White)}
        }
        TextButton(onClick = editClicked, modifier = Modifier.align(Alignment.BottomStart).padding(10.dp).clip(RoundedCornerShape(10.dp)).background(Color.White)) {
            Text("EDIT", modifier = Modifier.padding(5.dp), fontSize = 24.sp)
        }
        TextButton(onClick = closeClicked, modifier = Modifier.align(Alignment.BottomEnd).padding(10.dp).clip(RoundedCornerShape(10.dp)).background(Color.White)) {
            Text("Change Image", fontSize = 10.sp, modifier = Modifier.padding(5.dp))
        }
    }
}

@Preview
@Composable
fun PreviewBoardCardExpanded() {
    BoardCardExpanded(Board(R.drawable.sample_image, name = "heck", UUID.randomUUID()), closeClicked = {}, editClicked = {})
}