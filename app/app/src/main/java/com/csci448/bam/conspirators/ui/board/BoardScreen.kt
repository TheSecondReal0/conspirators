package com.csci448.bam.conspirators.ui.board

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Home

import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.csci448.bam.conspirators.DrawingViewModel.SelectedTool
import com.csci448.bam.conspirators.R
import com.csci448.bam.conspirators.data.AddedComponents
import com.csci448.bam.conspirators.data.AddedComponents.Companion.scale
import com.csci448.bam.conspirators.data.AddedComponents.Companion.screenSize
import com.csci448.bam.conspirators.viewmodel.ConspiratorsViewModel
import java.io.File
import java.util.concurrent.ExecutorService
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun BoardScreen(
    viewModel: ConspiratorsViewModel,
    modifier: Modifier,
    homeClicked: () -> Unit,
    shouldShowCamera: MutableState<Boolean>,
    outputDirectory: File,
    cameraExecutor: ExecutorService,
    handleImageCapture: (Uri) -> Unit
) {

    Log.i("SIZE", "Screen W: ${LocalConfiguration.current.screenWidthDp} H: ${LocalConfiguration.current.screenHeightDp}")
    // Various Variables, some may be better off in the VM later
    screenSize = Pair(LocalConfiguration.current.screenWidthDp, LocalConfiguration.current.screenHeightDp)
    var selectedTool by remember { viewModel.selectedTool}
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    // global transforming values for the board components
    var offset by remember { mutableStateOf(Offset.Zero) }
    var scale by remember { mutableFloatStateOf(1f) }
    var pointerOffset by remember { mutableStateOf(Offset(0f,0f)) }

    val currentContext = LocalContext.current
    val screenSize = Offset(currentContext.resources.displayMetrics.widthPixels.toFloat(),currentContext.resources.displayMetrics.heightPixels.toFloat())

    // This allows us to pick an image off the gallery
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri ->
            uri?.let {
                imageUri = it
                viewModel.conspiracyEvidences.add(AddedComponents(
                    uri = imageUri!!,
                    context = currentContext,
                    offset = mutableStateOf(-offset + Offset(currentContext.resources.displayMetrics.widthPixels.toFloat()/2f,
                        currentContext.resources.displayMetrics.heightPixels.toFloat()/2f)
                    )
                ))
            }
        }
    )
    /*
        Here is where the Board actually starts getting drawn. We have a canvas to draw on that can detect touch using 3 pointerInputs.
        PointerInputs denote the type of touch we're looking for.
            1st one used for scaling is clunky and idk why.
            2nd is for panning/ dragging images
            3rd is for tapping
        After configuring the Canvas, we actually draw in it.
        We're currently drawing the Images then the Lines.

        Overlaid on top of the board, we have buttons.
        The first chunk of buttons listed are often invisible except in specific circumstances.
        You'll then get to a column that houses the toolbox buttons. Home button has yet to be configured.
     */
    var showCam by remember { mutableStateOf(false) }
    if (showCam && shouldShowCamera.value && ContextCompat.checkSelfPermission(
            currentContext,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    ) {
        (println())
        CameraView(
            outputDirectory = outputDirectory,
            executor = cameraExecutor,
            onImageCaptured = {handleImageCapture; showCam = false},
            onError = Unit)
    } else {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            var itemToDrag: Int? = null
            Canvas(modifier = Modifier
                .fillMaxSize()
                .background(Color.DarkGray)
                // DETECTS ZOOM & PAN GESTURE
                .pointerInput(Unit) {
                    detectTransformGestures (panZoomLock = true) { centroid, pan, zoom, gestureRotate ->
                        viewModel.isZoomPercentShowing.value = true
                        var lockToZoomOnly = false
                        if (scale*zoom != scale){
                            lockToZoomOnly = true
                        }
                        scale *= zoom
                        viewModel.rescaleAllComponents(scale)
                        Log.i("Multi Drag", "multi drag start at ${centroid.x}, ${centroid.y}")
                        //offset = (offset + centroid / oldScale).rotateBy(gestureRotate) - (centroid / scale + pan / oldScale)
                        pointerOffset = centroid
                        Log.i("Drag", "      drag start at ${pointerOffset.x}, ${pointerOffset.y}")
                        // Finding target image to move if applicable
                        var removeIdx: Int? = null
                        if (!lockToZoomOnly) {
                            viewModel.conspiracyEvidences.forEachIndexed { index, item ->
                                if (pointerOffset.x >= (item.offset.value.x + offset.x) * scale &&
                                    pointerOffset.y >= (item.offset.value.y + offset.y) * scale &&
                                    pointerOffset.x <= item.getBitmap().width + (item.offset.value.x + offset.x) * scale &&
                                    pointerOffset.y <= item.getBitmap().height + (item.offset.value.y + offset.y) * scale
                                ) {
                                    removeIdx = index
                                    Log.i("listlen", "$removeIdx")
                                }
                            }
                        }
                        if (removeIdx != null) {
                            viewModel.conspiracyEvidences.add(viewModel.conspiracyEvidences[removeIdx!!])
                            viewModel.conspiracyEvidences.removeAt(removeIdx!!)
                            itemToDrag = viewModel.conspiracyEvidences.lastIndex
                        }

                        if (selectedTool == SelectedTool.EDIT || selectedTool == SelectedTool.TRASH || selectedTool == SelectedTool.CONNECT) {
                            // Moving Targeted Image
                            if (itemToDrag != null) {
                                val imageOffset  = viewModel.conspiracyEvidences[itemToDrag!!].offset
                                viewModel.conspiracyEvidences[itemToDrag!!].offset.value = Offset(imageOffset.value.x + pan.x, imageOffset.value.y + pan.y)
                                Log.i("Drag", "Image offset changing")
                                itemToDrag = null;
                            }
                            else {
                                offset+=pan*scale
                            }
                        }
                        else {
                            offset += pan*scale
                        }

                    }
                }
                //DETECTS TAPS FOR COMPONENT SELECTION
                .pointerInput(Unit) {
                    viewModel.isZoomPercentShowing.value = false
                    detectTapGestures(onTap = { pointerStartOffset -> pointerOffset = pointerStartOffset
                        Log.i("tapping", "detected tap")
                        var targetIdx: Int? = null
                        viewModel.conspiracyEvidences.forEachIndexed {
                                index, item ->
                            if (pointerOffset.x >=                           (item.offset.value.x + offset.x)*scale &&
                                pointerOffset.y >=                           (item.offset.value.y + offset.y)*scale &&
                                pointerOffset.x <= item.getBitmap().width  + (item.offset.value.x + offset.x)*scale &&
                                pointerOffset.y <= item.getBitmap().height + (item.offset.value.y + offset.y)*scale
                            )
                            {
                                if (selectedTool == SelectedTool.TRASH || selectedTool == SelectedTool.CONNECT) {
                                    targetIdx = index
                                    Log.i("tapping", "item selected = ${viewModel.conspiracyEvidences.last().currentlySelected.value}")
                                }

                                if (selectedTool == SelectedTool.EDIT) {
                                    selectedTool = SelectedTool.EDIT_COMPONENT
                                }
                            }
                        }

                        if (targetIdx != null) {
                            if (selectedTool == SelectedTool.TRASH) {
                                viewModel.conspiracyEvidences.add(viewModel.conspiracyEvidences[targetIdx!!])
                                viewModel.conspiracyEvidences.removeAt(targetIdx!!)
                                viewModel.conspiracyEvidences.last().currentlySelected.value = !viewModel.conspiracyEvidences.last().currentlySelected.value
                            }
                            var addedLine = false
                            if (selectedTool == SelectedTool.CONNECT) {
                                viewModel.conspiracyEvidences.add(viewModel.conspiracyEvidences[targetIdx!!])
                                viewModel.conspiracyEvidences.removeAt(targetIdx!!)
                                viewModel.conspiracyEvidences.forEach { evidence ->
                                    if (evidence.currentlySelected.value == true) {
                                        addedLine = true
                                        evidence.currentlySelected.value = false
                                        viewModel.editConnection(viewModel.conspiracyEvidences.last(), evidence)
                                    }
                                }
                                if (!addedLine) {
                                    viewModel.conspiracyEvidences.last().currentlySelected.value = !viewModel.conspiracyEvidences.last().currentlySelected.value
                                }
                            }
                        }
                    })
                }
            ) {
                // DRAW THE BOARD
                val canvasQuadrantSize = size/5f
                var shouldAddRecenterButton = true
                // DRAW IMAGES
                viewModel.conspiracyEvidences.forEach { item ->
                    val calculatedOffset = Offset((item.offset.value.x + offset.x)*scale, (item.offset.value.y + offset.y)*scale)
                    // check if the item is going to actually be on the screen so we dont waste resources trying to draw it
                    if (calculatedOffset.x < screenSize.x && calculatedOffset.y < screenSize.y &&
                        calculatedOffset.x + item.getBitmap().width > 0 &&
                        calculatedOffset.y + item.getBitmap().height > 0) {
                        //Log.i("compare", "Comp x ${calculatedOffset.x.toString()}, Board x: ${screenSize.x.toString()}")
                        //Log.i("compare", "Comp y ${calculatedOffset.y.toString()}, Board y: ${screenSize.y.toString()}")
                        shouldAddRecenterButton = false
                        item.getBitmap().prepareToDraw()
                        if(item.currentlySelected.value) {
                            drawRect(color = Color.White, topLeft = calculatedOffset - Offset(6f,6f),size = Size(item.getBitmap().width+12f, item.getBitmap().height+12f))
                            drawImage(item.getBitmap(), calculatedOffset, colorFilter = ColorFilter.tint(Color.Yellow, blendMode = BlendMode.Softlight))
                        }
                        else {
                            drawImage(item.getBitmap(),calculatedOffset)
                        }
                    }
                }
                // DRAW CONNECTING LINES
                viewModel.conspiracyConnections.forEach { item ->
                    val calculatedOffsetStart = Offset((item.addedComponent1.offset.value.x + offset.x)*scale + item.addedComponent1.getBitmap().width/2, (item.addedComponent1.offset.value.y + offset.y)*scale + item.addedComponent1.getBitmap().height/20)
                    val calculatedOffsetEnd = Offset((item.addedComponent2.offset.value.x + offset.x)*scale + item.addedComponent2.getBitmap().width/2, (item.addedComponent2.offset.value.y + offset.y)*scale + item.addedComponent2.getBitmap().height/20)
                    drawCircle(color = Color.Black, radius = 10f, center = calculatedOffsetEnd)
                    drawCircle(color = Color.Black, radius = 10f, center = calculatedOffsetStart)
                    drawLine(color = Color.Red, start = calculatedOffsetStart, end = calculatedOffsetEnd, strokeWidth = 5f)

                }
                if (shouldAddRecenterButton && viewModel.conspiracyEvidences.size > 0) {
                    viewModel.isRecenterButtonShowing.value = true
                    Log.i("New Button", "Recenter needs to show up")
                }
                else {
                    viewModel.isRecenterButtonShowing.value = false
                }
                //drawText()
            }

            // Optional Buttons on Top Middle of Screen that only appear on certain modes being used
            // trash disposal button
            if (viewModel.isEmptyTrashShowing.value && selectedTool == SelectedTool.TRASH) {
                SimpleTextButton(
                    onClick = {
                        val iterator1 = viewModel.conspiracyConnections.iterator()
                        while (iterator1.hasNext()) {
                            val element = iterator1.next()
                            if (element.addedComponent1.currentlySelected.value || element.addedComponent2.currentlySelected.value ) {
                                iterator1.remove()
                            }
                        }
                        val iterator = viewModel.conspiracyEvidences.iterator()
                        while (iterator.hasNext()) {
                            val element = iterator.next()
                            if (element.currentlySelected.value) {
                                iterator.remove()
                            }
                        }
                        viewModel.isEmptyTrashShowing.value = false
                        selectedTool = SelectedTool.EDIT
                    }, modifier = modifier.clip(shape = RoundedCornerShape(5.dp)).align(Alignment.TopCenter).background(Color.Gray),
                    label = "Delete selected items"
                )
            }
            // Top right corner buttons/notifs that only occasionally show
            Column(modifier = Modifier.align(Alignment.TopEnd)) {
                // recenter button
                if (viewModel.isRecenterButtonShowing.value)  {
                    SimpleTextButton(
                        onClick = {
                            val iterator = viewModel.conspiracyEvidences.iterator()

                            val componentOffsetList: MutableList<Offset> = mutableListOf()
                            while (iterator.hasNext()) {
                                val element = iterator.next()
                                componentOffsetList.add(element.offset.value)
                            }
                            componentOffsetList.sortBy { offset.x }
                            val newOffsetX = -componentOffsetList[componentOffsetList.size/2].x + screenSize.x/2
                            //componentOffsetList.sortBy { offset.y }
                            val newOffsetY = -componentOffsetList[componentOffsetList.size/2].y + screenSize.y/2
                            offset = Offset(newOffsetX, newOffsetY)
                            viewModel.isRecenterButtonShowing.value = false
                        }, modifier = modifier.padding(end = 10.dp).clip(shape = RoundedCornerShape(5.dp)).background(Color.Gray),
                        label = "Recenter"
                    )
                }
                // show zoom percent
                if (viewModel.isZoomPercentShowing.value)  {
                    Text(
                        modifier = modifier.padding(end = 10.dp).clip(shape = RoundedCornerShape(5.dp)).background(Color.Gray),
                        text = "${scale*100}%"
                    )
                }
            }
            // Column Carrying all editing tools
            Column (modifier = modifier
                .fillMaxWidth(.1F)
                .clip(shape = RoundedCornerShape(topEnd = 20.dp, bottomEnd = 20.dp))
                .background(colorResource(R.color.red_200))
                .align(Alignment.TopStart)){
                // Create Button to add an image
                ToolButton(
                    onClick = {
                        if (selectedTool != SelectedTool.EDIT) {
                            viewModel.conspiracyEvidences.forEach { item ->
                                item.currentlySelected.value = false
                            }
                        }
                        selectedTool = SelectedTool.EDIT
                        galleryLauncher.launch("image/*")
                    },
                    icon = ImageVector.vectorResource(R.drawable.baseline_image_24),
                    iconDesc = "Click"
                )
                if (ContextCompat.checkSelfPermission(
                        currentContext,
                        Manifest.permission.CAMERA
                    ) == PackageManager.PERMISSION_GRANTED) {
                    ToolButton(
                        onClick = {
                            if (selectedTool != SelectedTool.EDIT) {
                                viewModel.conspiracyEvidences.forEach { item ->
                                    item.currentlySelected.value = false
                                }
                            }
                            selectedTool = SelectedTool.EDIT
                            shouldShowCamera.value = true
                            showCam = true
                        },
                        icon = ImageVector.vectorResource(R.drawable.baseline_camera_alt_24),
                        iconDesc = "Click"
                    )
                }




                // Connect Line Button
                ToolButton(
                    onClick = {
                        if (selectedTool != SelectedTool.CONNECT) {
                            selectedTool = SelectedTool.CONNECT
                            viewModel.conspiracyEvidences.forEach { item ->
                                item.currentlySelected.value = false
                            }
                        }
                        else {
                            selectedTool = SelectedTool.EDIT
                        }

                    },
                    icon = ImageVector.vectorResource(R.drawable.baseline_draw_24),
                    useIcon2 = selectedTool == SelectedTool.CONNECT,
                    icon2 = Icons.Filled.Done,
                    iconDesc = "Click"
                )
                // view only mode button
                ToolButton(
                    onClick = {
                        selectedTool = if (selectedTool != SelectedTool.VIEW) {
                            SelectedTool.VIEW
                        } else {
                            SelectedTool.EDIT
                        }
                    },
                    icon = Icons.Filled.Create,
                    icon2 = ImageVector.vectorResource(R.drawable.eye),
                    useIcon2 = (selectedTool == SelectedTool.VIEW),
                    iconDesc = "Click"
                )
                // List View Button
                ToolButton(
                    onClick = {
                        Log.i("List","clicked")
                        viewModel.showListComponentTree.value = !viewModel.showListComponentTree.value
                    },
                    icon = Icons.AutoMirrored.Filled.List,
                    iconDesc = "Click"
                )
                // delete mode button
                ToolButton(
                    onClick = {
                        Log.i("trash","clicked")
                        if (selectedTool != SelectedTool.TRASH) {
                            Log.i("trash","Now in trash mode")
                            selectedTool = SelectedTool.TRASH
                            viewModel.isEmptyTrashShowing.value = true
                        } else {
                            Log.i("trash","Now in edit mode")
                            selectedTool = SelectedTool.EDIT
                            viewModel.isEmptyTrashShowing.value = false
                            viewModel.conspiracyEvidences.forEach { item ->
                                item.currentlySelected.value = false
                            }
                        }
                    },
                    icon = Icons.Filled.Delete,
                    iconDesc = "Click"
                )
                // home button
                ToolButton(
                    onClick = {
                        homeClicked()
                        Log.i("home","clicked")
                    },
                    icon = Icons.Filled.Home,
                    iconDesc = "Click"
                )
            }
            // Edit Component Card
            if (selectedTool == SelectedTool.EDIT_COMPONENT) {
                ComponentEditCard(
                    modifier = Modifier,
                    component = viewModel.conspiracyEvidences[0],
                    context = currentContext,
                    closeClicked = {
                        selectedTool = SelectedTool.EDIT
                    }
                )
            }

            val animatedAlpha: Float by animateFloatAsState(if
                                                                    (selectedTool == SelectedTool.EDIT ||
                selectedTool == SelectedTool.VIEW ||
                selectedTool == SelectedTool.CONNECT)
                1f else 0f, label = "alpha",
                animationSpec = tween(durationMillis = 500, easing = FastOutSlowInEasing))
            Text(
                text = getCurrentActionLabel(selectedTool),
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .graphicsLayer { alpha = animatedAlpha }
                    .padding(5.dp)
                    .background(Color.White)


            )
            //Image( painter = rememberAsyncImagePainter(imageUri), contentDescription = "image" , modifier = Modifier.fillMaxSize(.05f).align(Alignment.BottomCenter))
        }
    }

}

private fun getCurrentActionLabel(selectedTool: SelectedTool): String {
    return when (selectedTool) {
        SelectedTool.EDIT -> "Editing"
        SelectedTool.VIEW -> "Viewing"
        SelectedTool.CONNECT -> "Connecting Evidence"
        else -> ""
    }
}

@Composable
fun ComponentEditCard(modifier: Modifier, component: AddedComponents, context: Context, closeClicked: () -> Unit) {
    Column(
        modifier = modifier.fillMaxHeight(.65f).fillMaxWidth(0.8f).clip(RoundedCornerShape(20.dp)).background(Color.Cyan), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.SpaceEvenly
    ) {
            val inputStream = context.contentResolver.openInputStream(component.uri)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            inputStream?.close()
            var desiredHeight = bitmap.height.toFloat()
            var desiredWidth = bitmap.width.toFloat()

            if (desiredHeight > screenSize.second) {
                val scaler: Float = desiredHeight/(screenSize.second)
                desiredHeight /= scaler
                desiredWidth /= scaler
            }
            if (desiredWidth > screenSize.first) {
                val scaler = desiredWidth/(screenSize.first)
                desiredHeight /= scaler
                desiredWidth /= scaler
            }
            desiredWidth = desiredWidth* scale.floatValue
            desiredHeight = desiredHeight* scale.floatValue
            val bitmap2 = Bitmap.createScaledBitmap(bitmap, desiredWidth.toInt(), desiredHeight.toInt(), false)
            val imageBitmap = bitmap2.asImageBitmap()
        Row(horizontalArrangement = Arrangement.SpaceAround) {

            TextField(
                value = component.title.value,
                onValueChange = { newText -> component.title.value = newText },
                label = { Text("Title") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(.6f)
            )
            IconButton(
                onClick = closeClicked, modifier.size(40.dp),
                colors = IconButtonColors(
                    containerColor = Color(0,0,0,100),
                    contentColor = Color.White,
                    disabledContainerColor = Color.DarkGray,
                    disabledContentColor = Color.DarkGray
                )
            ) {
                Icon(Icons.Filled.Close, "close")
            }
        }

        Image(imageBitmap, component.title.value)
    }
}

fun Offset.rotateBy(angle: Float): Offset {
    val angleInRadians = angle * (PI / 180)
    val cos = cos(angleInRadians)
    val sin = sin(angleInRadians)
    return Offset((x * cos - y * sin).toFloat(), (x * sin + y * cos).toFloat())
}
