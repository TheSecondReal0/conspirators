package com.csci448.bam.conspirators.viewmodel


import android.net.Uri
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.geometry.Offset

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.csci448.bam.conspirators.DrawingViewModel.SelectedTool
import com.csci448.bam.conspirators.data.AddedComponent
import com.csci448.bam.conspirators.data.Board
import com.csci448.bam.conspirators.data.converters.ConnectionFB
import com.csci448.bam.conspirators.data.DrawnConnection
import com.csci448.bam.conspirators.data.User
import com.csci448.bam.conspirators.data.converters.ComponentFB
import com.csci448.bam.conspirators.data.firestore.Image
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

    private val mFirebaseBoards = mutableStateMapOf<String, com.csci448.bam.conspirators.data.firestore.Board>()
    val firebaseBoardValues: List<com.csci448.bam.conspirators.data.firestore.Board> get() = mFirebaseBoards.values.toList()
    init {
        storageService.getAllBoards(
            onSuccess = {
                it.forEach {id, board -> mFirebaseBoards[id] = board }
            },
            onError = {}
        )
    }

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
    val currentBoardComponents = mutableStateListOf<AddedComponent>()
    val isEmptyTrashShowing = mutableStateOf(false);
    val isRecenterButtonShowing = mutableStateOf(false)
    val isZoomPercentShowing = mutableStateOf(false)
    val searchExpanded = mutableStateOf(false)
    val showListComponentTree = mutableStateOf(false)
    val selectedTool = mutableStateOf(SelectedTool.EDIT)
    private val mConspiracyConnections = mutableListOf<DrawnConnection>()
    val conspiracyConnections = mConspiracyConnections
    val isSaveLoading = mutableStateOf(false)

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
        AddedComponent.scale.floatValue = scale
        currentBoardComponents.forEach { item ->
            item.rescale()
        }
    }

    fun editConnection(evidence1: AddedComponent, evidence2: AddedComponent) {
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

    // create new board page
    val currentBoardTitle = mutableStateOf<String>("")
    val currentThumbnailImage = mutableStateOf<Uri?>(null)
    fun createNewBoard() {
        // add board to database
        mBoard.value = com.csci448.bam.conspirators.data.firestore.Board(
            id = null,
            userId = mThisUser.value?.uid.toString(),
            name = currentBoardTitle.value,
        )
        Log.i(LOG_TAG, "board made for with UUID: ${mBoard.value!!.id.toString()} by ")
        //TODO I assume it automatically gets detected by FireStore list? but if not, update list
    }

    // update the current board that we're on
    fun saveCurrentBoardConfigAndUpload(){
        mergeNewImagesIntoBoard()
        mergeNewConnectionsIntoBoard()
        Log.i(LOG_TAG, "merges complete")
        currentBoardComponents.forEach { compn ->
            if(compn.uri != null) {//TODO this is a sloppy method of overwriting prevention
                compn.id.let {
                    storageService.uploadImage(
                        compn.uri!!,
                        fileName = it,
                        onSuccess = {
                            Log.i(LOG_TAG, "Img ${compn.id} successfully uploaded")
                        },
                        onError = {
                            Log.d(LOG_TAG, "Img ${compn.id} failed to upload")
                        }
                    )
                }
            }
        }
        val board = mBoard.value ?: return

        val boardToSave = board.copy(
            images = mBoard.value!!.images,
            connections = mBoard.value!!.connections,
        )
        Log.i(LOG_TAG, "merged again")
        if (board.id == null) {
            storageService.saveBoard(boardToSave,
                onSuccess = { savedBoard ->
                    mBoard.value = savedBoard
                    Log.i(LOG_TAG, "saved a new board")
                },
                onError = { e ->
                    Log.i(LOG_TAG, "failed to save a new board")
                    Log.d(LOG_TAG, e.printStackTrace().toString())
                }
            )

        } else {
            storageService.updateBoard(boardToSave) { error ->
                if (error == null) {
                    Log.i(LOG_TAG, "updated an old board")
                    isSaveLoading.value = false
                } else {
                    Log.i(LOG_TAG, "failed to update old board")
                    isSaveLoading.value = false
                }
            }
        }
        isSaveLoading.value = false
    }
    val uploadedUrls = mutableMapOf<String, String>() // componentId to downloadUrl

    private fun mergeNewImagesIntoBoard() {
        val board = mBoard.value ?: return
        val currentImagesMap = board.images.toMutableMap()
        for (component in currentBoardComponents) {
            if (component.id !in currentImagesMap) {
                Log.i(LOG_TAG, "adding component to firestore form")
                val firebaseImage = component.toFirebaseImage()
                if (firebaseImage != null && component.id != null) {
                    currentImagesMap[component.id] = firebaseImage
                }
            }
        }
        // board now updated with images
        mBoard.value = board.copy(images = currentImagesMap)
    }


    private fun AddedComponent.toFirebaseImage(): Image? {
        val url = this.url ?: return null // If it hasn't been uploaded yet, skip
        return Image(
            imageUrl = url,
            x = this.offset.value.x.toDouble(),
            y = this.offset.value.y.toDouble()
        )
    }

    fun pullCurrentBoardDataFromFB() {
        mBoard.value?.images?.forEach{ img ->
            currentBoardComponents.add(
                AddedComponent(
                    uri = null,
                    context = null,
                    offset = mutableStateOf(Offset(x = img.value.x.toFloat(), y = img.value.y.toFloat())),
                    title = mutableStateOf(""),
                    url = img.value.imageUrl
                )
            )
        }
//TODO finish this and above, make sure to check the values arent already added to our list
        mBoard.value?.connections?.forEach { conc ->
            val newConc = conc?.toAddedConnection()
            if (newConc != null && conspiracyConnections.find { drawnConnection -> drawnConnection.addedComponent2.id == newConc.addedComponent1.id }) {

            }
        }
    }

    private fun ComponentFB.toAddedComp() : AddedComponent {
        return AddedComponent(
            uri = null,
            context = null,
            offset = mutableStateOf(Offset(this.x.toFloat(), this.y.toFloat())),
            title = mutableStateOf(this.title),
            id = this.id,
            url = this.imageUrl
        )
    }

    private fun ConnectionFB.toAddedConnection() : DrawnConnection? {
        val id1 = currentBoardComponents.find { addedComponent -> addedComponent.id == this.componentId1 }
        val id2 = currentBoardComponents.find { addedComponent -> addedComponent.id == this.componentId2 }
        if(id1 != null && id2 != null)
        return DrawnConnection(
            addedComponent1 = id1,
            addedComponent2 = id2,
            label = this.label
        )
        return null
    }

    private fun mergeNewConnectionsIntoBoard() {
        val board = mBoard.value ?: return
        val currentConnectionsFB = mBoard.value!!.connections.toMutableList()
        for (component in conspiracyConnections) {
            val tryComp = component.toFirebaseConnection()
            if (tryComp !in currentConnectionsFB) {
                if (tryComp != null) {
                    currentConnectionsFB.add(tryComp)
                }
            }
        }
        // board now updated with connection
        mBoard.value = board.copy(connections = currentConnectionsFB)
    }

    private fun DrawnConnection.toFirebaseConnection(): ConnectionFB? {
        if(addedComponent1.id !=null && addedComponent2.id != null) {
            return ConnectionFB(
                this.addedComponent1.id,
                this.addedComponent2.id,
                this.label)
        }
        else return null
    }

    val showCameraView = mutableStateOf(false)

    val offset = mutableStateOf(Offset.Zero)

    companion object {
        var showCameraView: Boolean = false
        const val LOG_TAG = "VM"
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