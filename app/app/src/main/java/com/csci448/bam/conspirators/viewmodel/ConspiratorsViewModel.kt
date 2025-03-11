package com.csci448.bam.conspirators.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.csci448.bam.conspirators.DrawingViewModel.SelectedTool
import com.csci448.bam.conspirators.data.AddedComponents
import com.csci448.bam.conspirators.data.Board
import com.csci448.bam.conspirators.data.DrawnConnection
import com.csci448.bam.conspirators.data.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

data class DrawingState(
    val selectedTool: SelectedTool = SelectedTool.EDIT
)

class ConspiratorsViewModel(val boards: List<Board>, val users: List<User>): ViewModel() {
    val conspiracyEvidences = mutableStateListOf<AddedComponents>()
    val isEmptyTrashShowing = mutableStateOf(false);
    val isRecenterButtonShowing = mutableStateOf(false)
    val searchExpanded = mutableStateOf(false)
    private val mConspiracyConnections = mutableListOf<DrawnConnection>()
    val conspiracyConnections = mConspiracyConnections

    private val mBoardListState = MutableStateFlow(boards)
    val boardListState: StateFlow<List<Board>>
        get() = mBoardListState.asStateFlow()

    private val mUserListState = MutableStateFlow(users)
    val userListState: StateFlow<List<User>>
        get() = mUserListState.asStateFlow()
    val currentUser = mutableStateOf(users[0])
    fun getBoardsOfUser(userUUID: UUID): List<Board> {
        val boards: MutableList<Board> = mutableListOf()
        for (board in boardListState.value) {
            if (board.userUUID == userUUID) {
                boards.add(board)
            }
        }
        return boards.toList()
    }

    fun getUserNameByUUID(userUUID: UUID): String {
        var username: String = "Unknown"
        for (user in userListState.value) {
            if (user.userId == userUUID) {
                username = user.userName
            }
        }
        return username
    }

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