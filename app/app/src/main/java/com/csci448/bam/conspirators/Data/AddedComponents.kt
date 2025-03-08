package com.csci448.busche.testing.Data

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.MutableFloatState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.OffsetEffect
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalConfiguration

data class AddedComponents( val uri: Uri, private val context: Context, val offset: MutableState<Offset> = mutableStateOf(Offset.Zero)) {
    private lateinit var imageBitmap: ImageBitmap
    val currentlySelected = mutableStateOf(false)

    companion object {
        var screenSize: Pair<Int, Int> = Pair(0,0)
        val scale: MutableFloatState = mutableFloatStateOf(1f)
    }
    fun getBitmap(): ImageBitmap {
        return imageBitmap
    }
    init {
            try {
                val inputStream = context.contentResolver.openInputStream(uri)
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
                desiredWidth = desiredWidth*scale.floatValue
                desiredHeight = desiredHeight*scale.floatValue
                val bitmap2 = Bitmap.createScaledBitmap(bitmap, desiredWidth.toInt(), desiredHeight.toInt(), false)
                offset.value -= Offset(desiredWidth / 2, desiredHeight / 2)
                imageBitmap = bitmap2.asImageBitmap()
                Log.i("BoardComponent","new Component created")

            } catch (e: Exception) {
                e.printStackTrace()
            }
    }

    fun rescale() {
        try {
            val inputStream = context.contentResolver.openInputStream(uri)
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
            desiredWidth = desiredWidth*scale.floatValue
            desiredHeight = desiredHeight*scale.floatValue
            val bitmap2 = Bitmap.createScaledBitmap(bitmap, desiredWidth.toInt(), desiredHeight.toInt(), false)
            imageBitmap = bitmap2.asImageBitmap()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
