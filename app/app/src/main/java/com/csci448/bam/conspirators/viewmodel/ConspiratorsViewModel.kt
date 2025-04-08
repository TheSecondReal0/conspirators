package com.csci448.bam.conspirators.viewmodel

import android.content.Context
import android.icu.text.SimpleDateFormat
import android.net.Uri
import android.util.Log
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.core.content.ContextCompat

import androidx.lifecycle.ViewModel
import com.csci448.bam.conspirators.DrawingViewModel.SelectedTool
import com.csci448.bam.conspirators.data.AddedComponents
import com.csci448.bam.conspirators.data.Board
import com.csci448.bam.conspirators.data.DrawnConnection
import com.csci448.bam.conspirators.data.User
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.io.File
import java.util.Locale
import java.util.UUID
import java.util.concurrent.Executor
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

data class DrawingState(
    val selectedTool: SelectedTool = SelectedTool.EDIT
)

class ConspiratorsViewModel(val boards: List<Board>, val users: List<User>): ViewModel() {
    // current user
    private val mThisUser: MutableState<FirebaseUser?> = mutableStateOf(null)
    val thisUser get() = mThisUser.value
    fun setUser(fbUser: FirebaseUser?) {
        mThisUser.value = fbUser
    }
    // all related to board drawing
    val conspiracyEvidences = mutableStateListOf<AddedComponents>()
    val isEmptyTrashShowing = mutableStateOf(false);
    val isRecenterButtonShowing = mutableStateOf(false)
    val isZoomPercentShowing = mutableStateOf(false)
    val searchExpanded = mutableStateOf(false)
    val showListComponentTree = mutableStateOf(false)
    val selectedTool = mutableStateOf(SelectedTool.EDIT)
    private val mConspiracyConnections = mutableListOf<DrawnConnection>()
    val conspiracyConnections = mConspiracyConnections

    // related to board list
    private val mBoardListState = MutableStateFlow(boards)
    val boardListState: StateFlow<List<Board>>
        get() = mBoardListState.asStateFlow()

    // related to user lists
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

    // board editing functions
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


    val showCameraView = false

    companion object {
        var showCameraView: Boolean = false
    }


}