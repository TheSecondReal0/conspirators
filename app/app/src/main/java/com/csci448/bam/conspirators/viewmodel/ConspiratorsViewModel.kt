package com.csci448.bam.conspirators.viewmodel


import android.net.Uri
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.csci448.bam.conspirators.DrawingViewModel.SelectedTool
import com.csci448.bam.conspirators.data.AddedComponents
import com.csci448.bam.conspirators.data.Board
import com.csci448.bam.conspirators.data.DrawnConnection
import com.csci448.bam.conspirators.data.User
import com.csci448.bam.conspirators.data.firestore.StorageService
import com.csci448.bam.conspirators.data.firestore.StorageServiceImpl
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID

data class DrawingState(
    val selectedTool: SelectedTool = SelectedTool.EDIT
)

class ConspiratorsViewModel(val boards: List<Board>, val users: List<User>): ViewModel() {
    val database = Firebase.firestore
    val storageService: StorageService = StorageServiceImpl()

    val mBoards = mutableStateMapOf<String, com.csci448.bam.conspirators.data.firestore.Board>()

    val mFirebaseBoards = mutableStateMapOf<String, com.csci448.bam.conspirators.data.firestore.Board>()
    val firebaseBoardValues: List<com.csci448.bam.conspirators.data.firestore.Board> get() = mFirebaseBoards.values.toList()
    init {
        storageService.getAllBoards(
            onSuccess = {
                it.forEach {id, board -> mFirebaseBoards[id] = board }
            },
            onError = {}
        )
    }

    /*
    fun testDB() {
        database.collection("test").add(hashMapOf(
            "firstType" to "Beep",
            "secondType" to "Beepus"
        ))
            .addOnSuccessListener { documentReference ->
                Log.i("jlkjl", "DocumentSnapshot added with ID: ${documentReference.id}")
            }
            .addOnFailureListener { e ->
                Log.w("jlkjl", "Error adding document", e)
            }
    }

     */

    // current User
    private val mThisUser: MutableState<FirebaseUser?> = mutableStateOf(null)
    val thisUser get() = mThisUser.value
    fun setUser(fbUser: FirebaseUser?) {
        mThisUser.value = fbUser
    }

    private val mBoard: MutableState<com.csci448.bam.conspirators.data.firestore.Board?> = mutableStateOf(null)
    val board get() = mBoard.value
    fun loadBoard(id: String) {
        storageService.getBoard(
            boardId = id,
            onSuccess = { mBoard.value = it },
            onError = {}
        )
    }

    fun saveBoard(board: com.csci448.bam.conspirators.data.firestore.Board, onSuccess: (com.csci448.bam.conspirators.data.firestore.Board) -> Unit, onError: (Throwable) -> Unit) {
        storageService.saveBoard(
            board = board,
            onSuccess = onSuccess,
            onError = onError,
        )
    }

    fun updateBoard(board: com.csci448.bam.conspirators.data.firestore.Board, onResult: (Throwable?) -> Unit) {
        storageService.updateBoard(board, onResult = onResult)
    }

    fun deleteBoard(boardId: String, onResult: (Throwable?) -> Unit) {
        storageService.deleteBoard(boardId, onResult = onResult)
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

    fun addListenerForBoardsWithUserId(userId: String = "") {
        if (thisUser == null) {
            return
        }
        val id = thisUser!!.uid
        viewModelScope.launch() {
            storageService.addListenerForBoardsWithUserId(
                id,
                {wasDocumentDeleted: Boolean, board: com.csci448.bam.conspirators.data.firestore.Board -> onBoardsWithUserIdDocumentEvent(wasDocumentDeleted, board)},
                {})
        }
    }

    fun onBoardsWithUserIdDocumentEvent(wasDocumentDeleted: Boolean, board: com.csci448.bam.conspirators.data.firestore.Board) {
        if (wasDocumentDeleted) mBoards.remove(board.id!!) else mBoards[board.id!!] = board
    }

    fun removeListenerForBoardsWithUserId() {
        viewModelScope.launch() { storageService.removeListenerForBoardsWithUserId() }
    }

    // fileName doesn't need to be unique
    // onSuccess string parameter will be URL we can use to download the image again
    fun uploadImage(imageUri: Uri, fileName: String, onSuccess: (String) -> Unit, onError: (Throwable) -> Unit) {
        storageService.uploadImage(
            imageUri = imageUri,
            fileName = fileName,
            onSuccess = onSuccess,
            onError = onError)
    }
}