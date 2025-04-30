package com.csci448.bam.conspirators.data.firestore

import android.net.Uri

interface StorageService {
    fun addListenerForBoardsWithUserId(
        userId: String,
        onDocumentEvent: (Boolean, Board) -> Unit,
        onError: (Throwable) -> Unit
    )

    fun removeListenerForBoardsWithUserId()
    fun getBoard(boardId: String, onError: (Throwable) -> Unit, onSuccess: (Board) -> Unit)
    fun saveBoard(board: Board, onSuccess: (Board) -> Unit, onError: (Throwable) -> Unit)
    fun updateBoard(board: Board, onResult: (Throwable?) -> Unit)
    fun deleteBoard(boardId: String, onResult: (Throwable?) -> Unit)

    fun getAllBoards(onSuccess: (Map<String, Board>) -> Unit, onError: (Throwable) -> Unit)

    // fileName doesn't need to be unique
    fun uploadImage(imageUri: Uri, fileName: String, onSuccess: (String) -> Unit, onError: (Throwable) -> Unit)
}