package com.csci448.bam.conspirators.ui.createboard

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateColor
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.animateValue
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.csci448.bam.conspirators.R
import com.csci448.bam.conspirators.viewmodel.ConspiratorsViewModel


@Composable
fun CreateBoardScreen(modifier: Modifier,
                      viewModel: ConspiratorsViewModel,
                      navigateToBoardCreation: () -> Unit
) {
    val context = LocalContext.current

    // This allows us to pick an image off the gallery
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri ->
            uri?.let {
                viewModel.currentThumbnailImage.value = it
            }
        }
    )

    val titleEntryColors = TextFieldColors(
        focusedTextColor = Color.Black,
        unfocusedTextColor = Color.Black,
        focusedContainerColor = Color.White,
        unfocusedContainerColor =  Color(90,70,70),
        cursorColor =  colorResource(R.color.red_200),
        textSelectionColors = TextSelectionColors(
            handleColor =  colorResource(R.color.red_200),
            backgroundColor =  colorResource(R.color.red_200)
        ),
        focusedIndicatorColor =  colorResource(R.color.red_200),
        unfocusedIndicatorColor = Color.White,
        focusedLeadingIconColor = Color.Black,
        unfocusedLeadingIconColor = Color.Black,
        focusedTrailingIconColor = Color.Black,
        unfocusedTrailingIconColor = Color.Black,
        focusedLabelColor = Color.Black,
        unfocusedLabelColor = Color.Black,
        focusedPlaceholderColor = Color.White,
        unfocusedPlaceholderColor = Color.White,
        focusedSupportingTextColor =  colorResource(R.color.red_200),
        unfocusedSupportingTextColor =  colorResource(R.color.red_200),
        focusedPrefixColor = Color.Black,
        unfocusedPrefixColor = Color.Black,
        focusedSuffixColor = Color.Black,
        disabledTextColor = Color.Black,
        errorTextColor = Color.Black,
        disabledContainerColor = Color.DarkGray,
        errorContainerColor = Color.DarkGray,
        errorCursorColor = Color.Black,
        disabledIndicatorColor = Color.Black,
        errorIndicatorColor = Color.Black,
        disabledLeadingIconColor = Color.Black,
        errorLeadingIconColor = Color.Black,
        disabledTrailingIconColor = Color.Black,
        errorTrailingIconColor = Color.Black,
        disabledLabelColor = Color.Black,
        errorLabelColor = Color.Black,
        disabledPlaceholderColor = Color.Black,
        errorPlaceholderColor = Color.Black,
        disabledSupportingTextColor = Color.Black,
        errorSupportingTextColor = Color.Black,
        disabledPrefixColor = Color.Black,
        errorPrefixColor = Color.Black,
        unfocusedSuffixColor = Color.Black,
        disabledSuffixColor = Color.Black,
        errorSuffixColor = Color.Black
    )

    val infiniteTransition = rememberInfiniteTransition(label = "infinite")

    val color by infiniteTransition.animateColor(
        initialValue = Color(90, 30, 30),
        targetValue = Color(28, 16, 16),
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "color"
    )

    val border by infiniteTransition.animateValue(
        initialValue = 3.dp,
        targetValue = 6.dp,
        typeConverter = Dp.VectorConverter,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "border"
    )

    Box(modifier = Modifier
        .fillMaxSize()
        .background(color),
        contentAlignment = Alignment.Center) {
        Text(stringResource(R.string.board_create_page_title),
            fontSize = 36.sp,
            modifier = Modifier.align(Alignment.TopCenter).padding(20.dp),
            color = Color.White,
            textAlign = TextAlign.Center)
        Box(modifier = Modifier
            .fillMaxHeight(.8f)
            .fillMaxWidth(.9f)
            .clip(RoundedCornerShape(20.dp-border))
            .background(Color(100, 95, 95, 50))
            .border(border, colorResource(R.color.red_200)),
            contentAlignment = Alignment.Center
        ) {
            TextField(
                value = viewModel.currentBoardTitle.value,
                onValueChange = {if(it.length < 20) viewModel.currentBoardTitle.value = it},
                label = {Text("Title")},
                placeholder = {Text("My conspiracy")},
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth(.85f)
                    .padding(10.dp)
                    .align(Alignment.TopCenter),
                colors = titleEntryColors,
                shape = RoundedCornerShape(10.dp)
            )
            val thumbnailImage = viewModel.currentThumbnailImage.value
            if (thumbnailImage != null) {
                Box(
                    contentAlignment = Alignment.BottomStart
                ) {
                    Image(rememberAsyncImagePainter(thumbnailImage), null, alignment = Alignment.Center,
                        contentScale = ContentScale.Inside,
                        modifier = Modifier
                            .fillMaxWidth(.9f)
                            .fillMaxHeight(.5f)
                            .clip(RoundedCornerShape(5.dp))
                    )
                    IconButton(
                        onClick = {galleryLauncher.launch("image/*")},
                        modifier = Modifier.padding(5.dp).padding(5.dp).align(Alignment.BottomStart),
                        enabled = true,
                    ) {
                        Icon(Icons.Filled.AddPhotoAlternate, null)
                    }
                }

            }
            else {
                Box(
                    contentAlignment = Alignment.Center,
                            modifier = Modifier.fillMaxWidth(.8f).fillMaxHeight(.7f)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight()
                            .clip(RoundedCornerShape(5.dp))
                            .background(Color.LightGray)
                            .align(Alignment.Center)
                    )
                    IconButton(
                        onClick = {galleryLauncher.launch("image/*")},
                        modifier = Modifier.padding(5.dp).align(Alignment.BottomStart),
                        enabled = true,
                    ) {
                        Icon(Icons.Filled.AddPhotoAlternate, null)
                    }
                }
            }

            Row(horizontalArrangement = Arrangement.SpaceEvenly, modifier = Modifier.fillMaxWidth().padding(20.dp).align(Alignment.BottomCenter)) {
                Button(
                    onClick = {
                        viewModel.createNewBoard()
                        navigateToBoardCreation()
                              },
                    modifier = Modifier,
                    enabled = viewModel.currentBoardTitle.value.isNotEmpty(),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonColors(
                        containerColor = colorResource(R.color.red_200),
                        contentColor = colorResource(R.color.white),
                        disabledContainerColor = colorResource(R.color.brown_200),
                        disabledContentColor = colorResource(R.color.brown_700)
                    ),
                    border = BorderStroke(2.dp, colorResource(R.color.red_700)),
                    contentPadding = PaddingValues(5.dp)
                ) {
                    Text("Create New Board")
                }
            }
        }
    }
}

