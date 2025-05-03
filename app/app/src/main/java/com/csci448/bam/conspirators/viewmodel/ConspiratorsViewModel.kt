package com.csci448.bam.conspirators.viewmodel


import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.geometry.Offset

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.csci448.bam.conspirators.DrawingViewModel.SelectedTool
import com.csci448.bam.conspirators.R
import com.csci448.bam.conspirators.data.AddedComponent
import com.csci448.bam.conspirators.data.OldBoard
import com.csci448.bam.conspirators.data.converters.ConnectionFB
import com.csci448.bam.conspirators.data.DrawnConnection
import com.csci448.bam.conspirators.data.User
import com.csci448.bam.conspirators.data.converters.ComponentFB
import com.csci448.bam.conspirators.data.firestore.Board
import com.csci448.bam.conspirators.data.firestore.Image
import com.csci448.bam.conspirators.data.firestore.StorageService
import com.csci448.bam.conspirators.data.firestore.StorageServiceImpl
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File
import java.util.UUID
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

data class DrawingState(
    val selectedTool: SelectedTool = SelectedTool.EDIT
)

class ConspiratorsViewModel(val boards: List<OldBoard>, val users: List<User>): ViewModel() {

    val imageNeedsNewUpload = mutableStateOf(false)
    val database = Firebase.firestore
    val storageService: StorageService = StorageServiceImpl()

    private val mMyBoards = mutableStateListOf<Board>()
    val myBoards get() = mMyBoards.toList()

    private val mFirebaseBoards = mutableStateMapOf<String, Board>()
    private val mAllBoards = mutableStateOf<List<Board>>(emptyList())
    val allBoards: State<List<Board>> get() = mAllBoards
    val firebaseBoardValues: MutableList<Board> get() = mFirebaseBoards.values.toMutableList()
    init {
        storageService.getAllBoards(
            onSuccess = {
                it.forEach {id, board -> mFirebaseBoards[id] = board
                    Log.i(LOG_TAG, "retrieved board ${id.toString()}")
                }
            },
            onError = {
                Log.i(LOG_TAG, "unable to retrieve boards from firebase")
            }
        )
        getUsersBoardsFromAllBoards()
    }

    fun refreshLocalScreenWithFirBaseData() {
        storageService.getAllBoards(
            onSuccess = { boards ->
                mFirebaseBoards.clear()
                boards.forEach { id, board ->
                    mFirebaseBoards[id] = board
                    Log.i(LOG_TAG, "retrieved board $id")
                }
                mAllBoards.value = mFirebaseBoards.values.toList()
            },
            onError = {
                Log.i(LOG_TAG, "unable to retrieve boards from firebase")
            }
        )
    }

    fun getUsersBoardsFromAllBoards() {
        mMyBoards.clear()
        mFirebaseBoards.forEach{ b->
            if (b.value.userId == mThisUser.value?.uid) {
                Log.i(LOG_TAG, "retrieved user's board ${b.value.id.toString()}")
                mMyBoards.add(b.value)
            }
        }
    }

    // current User
    private val mThisUser: MutableState<FirebaseUser?> = mutableStateOf(null)
    val thisUser get() = mThisUser.value
    fun setUser(fbUser: FirebaseUser?) {
        mThisUser.value = fbUser
    }

    private val mBoard: MutableState<Board?> = mutableStateOf(null)
    val board get() = mBoard.value

    fun loadBoard(id: String) {
        storageService.getBoard(
            boardId = id,
            onSuccess = { mBoard.value = it },
            onError = {}
        )
    }



    fun saveBoard(board: Board, onSuccess: (Board) -> Unit, onError: (Throwable) -> Unit) {
        storageService.saveBoard(
            board = board,
            onSuccess = onSuccess,
            onError = onError,
        )
    }

    fun updateBoard(board: Board, onResult: (Throwable?) -> Unit) {
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
    val currentBoardConnections = mConspiracyConnections
    val isSaveLoading = mutableStateOf(false)
    val detailComponent = mutableStateOf<AddedComponent?>(null)

    // related to board list
    private val mBoardListState = MutableStateFlow(boards)
    val boardListState: StateFlow<List<OldBoard>>
        get() = mBoardListState.asStateFlow()

    // related to user lists
    private val mUserListState = MutableStateFlow(users)
    val userListState: StateFlow<List<User>>
        get() = mUserListState.asStateFlow()
    val currentUser = mutableStateOf(users[0])

    fun getBoardsOfUser(userUUID: UUID): List<OldBoard> {
        val boards: MutableList<OldBoard> = mutableListOf()
        for (board in boardListState.value) {
            if (board.userUUID == userUUID) {
                boards.add(board)
            }
        }
        return boards.toList()
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
        // add thumbnail image to database but just set current board to local mBoard
        if (currentThumbnailImage.value != null) {
            uploadImage(
                imageUri = currentThumbnailImage.value!!,
                fileName = "Thumbnails",
                onSuccess = { it->
                    Log.i(LOG_TAG, "img thumbnail ${it.toString()} uploaded")
                    mBoard.value = Board(
                        id = null,
                        userId = mThisUser.value?.uid.toString(),
                        name = currentBoardTitle.value,
                        thumbnailImageUrl = it
                    )
                    saveBoard(
                        mBoard.value!!, onSuccess = { b ->
                            mBoard.value = b
                        },
                        onError = {})
                },
                onError = {
                    Log.i(LOG_TAG, "img thumbnail failed upload")
                }
            )
        }
        else {
            Log.i(LOG_TAG, "no thumbnail but new board")
            mBoard.value = Board(
                id = null,
                userId = mThisUser.value?.uid.toString(),
                name = currentBoardTitle.value,
                thumbnailImageUrl = null
            )
            saveBoard(
                mBoard.value!!, onSuccess = { b ->
                mBoard.value = b

            },
                onError = {})
        }

    }

    // update the current board that we're on, this should only be used with save function since it does not edit name or image
    fun saveCurrentBoardConfigAndUpload(){
        Log.i(LOG_TAG, "saveCurrentBoardConfigAndUpload() called, board: ${mBoard.value}")
        mergeNewImagesIntoBoard()
        mergeNewConnectionsIntoBoard()
        Log.i(LOG_TAG, "saveCurrentBoardConfigAndUpload() merges complete: ${mBoard.value}")
//        currentBoardComponents.forEach { compn ->
//            if(compn.uri != null) {//TODO this is a sloppy method of overwriting prevention
//                compn.id.let {
//                    storageService.uploadImage(
//                        compn.uri!!,
//                        fileName = it,
//                        onSuccess = {
//                            Log.i(LOG_TAG, "Img ${compn.id} successfully uploaded")
//                        },
//                        onError = {
//                            Log.d(LOG_TAG, "Img ${compn.id} failed to upload")
//                        }
//                    )
//                }
//            }
//        }
        var board = mBoard.value ?: return
        board = board.copy(userName = thisUser?.displayName ?: "Guest")

        val boardToSave = board
//            .copy(
//            images = mBoard.value!!.images,
//            connections = mBoard.value!!.connections,
//        )
//        Log.i(LOG_TAG, "merged again")
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

    private fun mergeNewImagesIntoBoard() {
        Log.d(LOG_TAG, "mergeNewImagesIntoBoard() called, board: ${mBoard.value}")
        val board = mBoard.value ?: return
        val currentImagesMap = board.images.toMutableMap()
        currentImagesMap.clear()
        Log.d(LOG_TAG, "About to merge new images into board, currentImagesMap: $currentImagesMap")
        for (component in currentBoardComponents) {
            if (!currentImagesMap.containsKey(component.id)) {
                Log.i(LOG_TAG, "adding component to firestore form")
                val firebaseImage = component.toFirebaseImage()
                if (firebaseImage != null && component.id != null) {
                    currentImagesMap[component.id] = firebaseImage
                }
            }
        }
        Log.d(LOG_TAG, "merging new images into board, currentImagesMap: $currentImagesMap")
        // board now updated with images
        mBoard.value = board.copy(images = currentImagesMap)
        Log.d(LOG_TAG, "Merged new images into board, new board: ${mBoard.value}")
    }


    private fun AddedComponent.toFirebaseImage(): Image? {
        val url = this.url ?: return null // If it hasn't been uploaded yet, skip
        return Image(
            imageUrl = url,
            x = this.offset.value.x.toDouble(),
            y = this.offset.value.y.toDouble()
        )
    }

    // sets the selected board up for editing/viewing
    fun getBoardFromFBBoardListForEditing(editBoard: Board) {
        mBoard.value = editBoard
        currentBoardComponents.clear()
        currentBoardConnections.clear()
        pullCurrentBoardDataFromFBBoard()
    }

    private fun pullCurrentBoardDataFromFBBoard() {
        // pull in images and swap them to editable components if not already in editable connection list
        mBoard.value?.images?.forEach{ img ->
            val newComp = AddedComponent(
                id =img.key,
                uri = null,
                context = null,
                offset = mutableStateOf(Offset(x = img.value.x.toFloat(), y = img.value.y.toFloat())),
                title = mutableStateOf(""),
                url = img.value.imageUrl
            )
            viewModelScope.launch(Dispatchers.IO) {
                newComp.retrieveBitmap()
            }
            if (currentBoardComponents.find { return@find(it.id == newComp.id)} == null) {
                currentBoardComponents.add(
                    newComp
                )
            }
        }
        // pull in FB connections and swap them to editable connections
        mBoard.value?.connections?.forEach { conc ->
            val newConc = conc.toAddedConnection()
            // make sure newConc isnt null and that it does not already exist in the list
//            if (newConc != null && currentBoardConnections.find { return@find (it.addedComponent1.id == newConc.addedComponent1.id) } == null
//                && currentBoardConnections.find { return@find (it.addedComponent2.id == newConc.addedComponent2.id) } == null) {
            if (newConc != null) {
                currentBoardConnections.add(newConc)
            }
        }
        currentBoardTitle.value = mBoard.value?.name ?: ""

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
        currentConnectionsFB.clear()
        for (component in currentBoardConnections) {
            val tryComp = component.toFirebaseConnection()!!
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
                {wasDocumentDeleted: Boolean, board: Board -> onBoardsWithUserIdDocumentEvent(wasDocumentDeleted, board)},
                {})
        }
    }

    fun onBoardsWithUserIdDocumentEvent(wasDocumentDeleted: Boolean, board: Board) {
        if (wasDocumentDeleted) mFirebaseBoards.remove(board.id!!) else mFirebaseBoards[board.id!!] = board
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

    fun updateBoardNameAndImageToFB(boardToView: Board) {

        if (currentThumbnailImage.value != null && boardToView.id != null && currentThumbnailImage.value !=null && imageNeedsNewUpload.value) {
            imageNeedsNewUpload.value = false
            uploadImage(
                imageUri = currentThumbnailImage.value!!,
                fileName = boardToView.id ?: "ERROR",
                onSuccess = { it->
                    Log.i(LOG_TAG, "img thumbnail ${it.toString()} uploaded")
                    mBoard.value = boardToView.copy(
                        name = currentBoardTitle.value,
                        userName = thisUser?.displayName ?: "User",
                        thumbnailImageUrl = it
                    )
                    updateBoard(
                        mBoard.value!!,
                        onResult = {
                            Log.i(LOG_TAG, "Updated board to new title and image")
                        }
                    )
                },
                onError = {
                    Log.i(LOG_TAG, "img thumbnail failed upload")
                }
            )
        }
        else if(boardToView.id != null) {
            Log.i(LOG_TAG, "no thumbnail but new update")
            mBoard.value = boardToView.copy(
                name = currentBoardTitle.value,
                userName = thisUser?.displayName ?: "User",
                thumbnailImageUrl = null
            )
            updateBoard(
                mBoard.value!!,
                onResult = {
                    Log.i(LOG_TAG, "Updated board to new title and image")
                }
            )
        }
        refreshLocalScreenWithFirBaseData()
    }



//    private lateinit var outputDirectory: File
    internal var cameraExecutor: ExecutorService = Executors.newSingleThreadExecutor()
    internal var shouldShowCamera: MutableState<Boolean> = mutableStateOf(false)
    private lateinit var photoUri: Uri
    private var shouldShowPhoto: MutableState<Boolean> = mutableStateOf(false)

    fun handleImageCapture(uri: Uri, currentContext: Context) {
        Log.i("kilo", "Image captured: $uri")
        shouldShowCamera.value = false
        photoUri = uri
        shouldShowPhoto.value = true
        uploadImage(
            imageUri = uri,
            fileName = "photo",
            onSuccess = {url ->
                var component =
                    AddedComponent(
                        uri = photoUri,
                        url = url,
                        context = currentContext,
                        offset = mutableStateOf(-offset.value + Offset(currentContext.resources.displayMetrics.widthPixels.toFloat()/2f,
                            currentContext.resources.displayMetrics.heightPixels.toFloat()/2f)
                        )
                    )
                currentBoardComponents.add(component)
                viewModelScope.launch(Dispatchers.IO) {
                    component.retrieveBitmap()
                }
            },
            onError = {e ->
                Log.d(LOG_TAG, "Failed to upload picture taken from camera")
            }
        )
    }
}