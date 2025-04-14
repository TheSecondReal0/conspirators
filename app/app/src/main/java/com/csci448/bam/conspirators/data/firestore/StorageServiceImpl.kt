package com.csci448.bam.conspirators.data.firestore

import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.toObject
import com.google.firebase.ktx.Firebase

class StorageServiceImpl : StorageService {
    var listenerRegistration: ListenerRegistration? = null

    override fun addListener(
        userId: String,
        onDocumentEvent: (Boolean, Board) -> Unit,
        onError: (Throwable) -> Unit
    ) {
        val query = Firebase.firestore.collection("boards").whereEqualTo("userId", userId)

        listenerRegistration = query.addSnapshotListener { value, error ->
            if (error != null) {
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

    override fun removeListener() {
        listenerRegistration?.remove()
    }

    override fun getBoard(
        boardId: String,
        onError: (Throwable) -> Unit,
        onSuccess: (Board) -> Unit
    ) {
        TODO("Not yet implemented")
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

    private fun docToBoard(doc: QueryDocumentSnapshot): Board {
        var obj: Board = Board(id = doc.id)
        obj = obj.copy(name = doc.data.get("name").toString())
        return obj
    }
}