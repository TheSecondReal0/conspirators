package com.csci448.bam.conspirators.data.firestore

interface StorageService {
    fun addListener(
        userId: String,
        onDocumentEvent: (Boolean, Board) -> Unit,
        onError: (Throwable) -> Unit
    )

    fun removeListener()
    fun getBoard(boardId: String, onError: (Throwable) -> Unit, onSuccess: (Board) -> Unit)
    fun saveBoard(board: Board, onResult: (Throwable?) -> Unit)
    fun updateBoard(board: Board, onResult: (Throwable?) -> Unit)
    fun deleteBoard(boardId: String, onResult: (Throwable?) -> Unit)
}