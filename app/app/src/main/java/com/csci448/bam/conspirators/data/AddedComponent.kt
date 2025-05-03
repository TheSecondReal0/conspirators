package com.csci448.bam.conspirators.data

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableFloatState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.unit.Dp
import kotlinx.coroutines.coroutineScope
import java.net.URL
import java.util.UUID

data class AddedComponent(var uri: Uri? = null,
                          private val context: Context?,
                          val offset: MutableState<Offset> = mutableStateOf(Offset.Zero),
                          val title: MutableState<String> = mutableStateOf(""),
                          val id: String = UUID.randomUUID().toString(),
                          var url: String? = null) {
    private var imageBitmap: MutableState<ImageBitmap?> = mutableStateOf(null)
    val currentlySelected = mutableStateOf(false)

    companion object {
        var screenSize: Pair<Int, Int> = Pair(0,0)
        val scale: MutableFloatState = mutableFloatStateOf(1f)
    }
    fun getBitmap(): ImageBitmap? {
//        retrieveBitmap()
//        return imageBitmap
        return imageBitmap.value
    }

    suspend fun retrieveBitmap() {
        try {
            var bitmap: Bitmap? = null

            if (uri != null && context !=null) {
                val inputStream = context.contentResolver.openInputStream(uri!!)
                bitmap = BitmapFactory.decodeStream(inputStream)
                inputStream?.close()
            } else if (url != null) {

                val connection = URL(url).openConnection()
                connection.connect()
                val inputStream = connection.getInputStream()
                bitmap = BitmapFactory.decodeStream(inputStream)
                inputStream.close()

            } else {
                bitmap = null
            }

            if (bitmap != null) {
                var desiredHeight = bitmap.height.toFloat()
                var desiredWidth = bitmap.width.toFloat()

                if (desiredHeight > screenSize.second) {
                    val scaler: Float = desiredHeight / (screenSize.second)
                    desiredHeight /= scaler
                    desiredWidth /= scaler
                }
                if (desiredWidth > screenSize.first) {
                    val scaler = desiredWidth / (screenSize.first)
                    desiredHeight /= scaler
                    desiredWidth /= scaler
                }
                desiredWidth = desiredWidth * scale.floatValue
                desiredHeight = desiredHeight * scale.floatValue
                val bitmap2 = Bitmap.createScaledBitmap(
                    bitmap,
                    desiredWidth.toInt(),
                    desiredHeight.toInt(),
                    false
                )
                offset.value -= Offset(desiredWidth / 2, desiredHeight / 2)
                imageBitmap.value = bitmap2.asImageBitmap()
                Log.i("BoardComponent", "new Component created")
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun rescale() {
        try {
            var bitmap: Bitmap? = null
            if (uri != null && context!=null) {
                val inputStream = context.contentResolver.openInputStream(uri!!)
                bitmap = BitmapFactory.decodeStream(inputStream)
                inputStream?.close()
            } else if (url != null) {

                val connection = URL(url).openConnection()
                connection.connect()
                val inputStream = connection.getInputStream()
                bitmap = BitmapFactory.decodeStream(inputStream)
                inputStream.close()

            } else {
                bitmap = null
            }
            if (bitmap != null) {
                var desiredHeight = bitmap.height.toFloat()
                var desiredWidth = bitmap.width.toFloat()

                if (desiredHeight > screenSize.second) {
                    val scaler: Float = desiredHeight / (screenSize.second)
                    desiredHeight /= scaler
                    desiredWidth /= scaler
                }
                if (desiredWidth > screenSize.first) {
                    val scaler = desiredWidth / (screenSize.first)
                    desiredHeight /= scaler
                    desiredWidth /= scaler
                }
                desiredWidth = desiredWidth * scale.floatValue
                desiredHeight = desiredHeight * scale.floatValue
                val bitmap2 = Bitmap.createScaledBitmap(
                    bitmap,
                    desiredWidth.toInt(),
                    desiredHeight.toInt(),
                    false
                )
                imageBitmap.value = bitmap2.asImageBitmap()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
