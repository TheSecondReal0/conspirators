package com.csci448.bam.conspirators.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.csci448.bam.conspirators.DrawingViewModel.SelectedTool
import com.csci448.bam.conspirators.data.AddedComponents
import com.csci448.bam.conspirators.data.DrawnConnection

data class DrawingState(
    val selectedTool: SelectedTool = SelectedTool.EDIT
)

class ConspiratorsViewModel: ViewModel() {
    val conspiracyEvidences = mutableStateListOf<AddedComponents>()
    val isEmptyTrashShowing = mutableStateOf(false);
    val isRecenterButtonShowing = mutableStateOf(false)
    private val mConspiracyConnections = mutableListOf<DrawnConnection>()
    val conspiracyConnections = mConspiracyConnections

    fun rescaleAllComponents(scale: Float) {
        AddedComponents.scale.floatValue = scale
        conspiracyEvidences.forEach { item ->
            item.rescale()
        }
    }

    fun editConnection(evidence1: AddedComponents, evidence2: AddedComponents) {
        val iterator = mConspiracyConnections.iterator()
        var addComp = true
        while (iterator.hasNext()) {
            val connection = iterator.next()
            if ((connection.addedComponent1 == evidence1 && connection.addedComponent2 == evidence2) ||
                (connection.addedComponent2 == evidence1 && connection.addedComponent1 == evidence2) ) {
                addComp = false
                iterator.remove()
            }
        }
        if (addComp == true) {
            mConspiracyConnections.add(DrawnConnection(evidence1, evidence2))
        }
    }
}