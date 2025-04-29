package com.csci448.bam.conspirators.data.firestore

import android.util.Log
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class StorageServiceImpl : StorageService {
    companion object {
        const val LOG_TAG: String = "StorageServiceImpl"
    }

    var listenerRegistrationForBoardsWithUserId: ListenerRegistration? = null

    override fun addListenerForBoardsWithUserId(
        userId: String,
        onDocumentEvent: (Boolean, Board) -> Unit,
        onError: (Throwable) -> Unit
    ) {
        val query = Firebase.firestore.collection("boards").whereEqualTo("userId", userId)

        listenerRegistrationForBoardsWithUserId = query.addSnapshotListener { value, error ->
            if (error != null) {
                Log.e(LOG_TAG, "listenerForBoardsWithUserId failed with error $error")
                onError(error)
                return@addSnapshotListener
            }

            value?.documentChanges?.forEach {
                val wasDocumentDeleted = it.type == DocumentChange.Type.REMOVED
                val board = docToBoard(it.document)

                onDocumentEvent(wasDocumentDeleted, board)
            }
        }
    }

    override fun removeListenerForBoardsWithUserId() {
        listenerRegistrationForBoardsWithUserId?.remove()
    }

    override fun getBoard(
        boardId: String,
        onError: (Throwable) -> Unit,
        onSuccess: (Board) -> Unit
    ) {
        val docRef = Firebase.firestore.collection("boards").document(boardId)
        docRef.get().addOnSuccessListener {
            Log.d(LOG_TAG, "Successfully retrieved board $boardId: ${it.data}")
            val board: Board = docToBoard(it)
            onSuccess(board)
        } .addOnFailureListener {
            Log.e(LOG_TAG, "getBoard failed with error $it")
            onError(it)
        }
    }

    override fun saveBoard(board: Board, onResult: (Throwable?) -> Unit) {
        TODO("Not yet implemented")
    }

    override fun updateBoard(board: Board, onResult: (Throwable?) -> Unit) {
        TODO("Not yet implemented")
    }

    override fun deleteBoard(boardId: String, onResult: (Throwable?) -> Unit) {
        TODO("Not yet implemented")
    }

    private fun docToBoard(doc: DocumentSnapshot): Board {
        var obj: Board = Board(id = doc.id)
        obj = obj.copy(name = doc.data?.get("name").toString())
        return obj
    }
}