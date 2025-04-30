package com.csci448.bam.conspirators.data.firestore

import android.net.Uri
import android.util.Log
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage

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

    override fun saveBoard(board: Board, onSuccess: (Board) -> Unit, onError: (Throwable) -> Unit) {
        Firebase.firestore.collection("boards")
            .add(board)
            .addOnSuccessListener { documentRef ->
                // need to pull generated ID back out
                val generatedId = documentRef.id
                Log.d(LOG_TAG, "Saved board with id: $generatedId")

                val newBoard: Board = board.copy(id = generatedId)
                onSuccess(newBoard)
            }
            .addOnFailureListener { e ->
                Log.e(LOG_TAG, "Error saving board", e)
                onError(e)
            }
    }

    override fun updateBoard(board: Board, onResult: (Throwable?) -> Unit) {
        Firebase.firestore.collection("boards")
            .document(board.id!!)
            .set(board)
            .addOnSuccessListener {
                onResult(null)
            }
            .addOnFailureListener {
                onResult(it)
            }
    }

    override fun deleteBoard(boardId: String, onResult: (Throwable?) -> Unit) {
        Firebase.firestore.collection("boards")
            .document(boardId)
            .delete()
            .addOnSuccessListener {
                onResult(null)
            }
            .addOnFailureListener {
                onResult(it)
            }
    }

    private fun docToBoard(doc: DocumentSnapshot): Board {
        var obj: Board? = doc.toObject(Board::class.java)
        if (obj == null) return Board()
        obj = obj!!.copy(
            id = doc.id
        )
        return obj
    }

    override fun uploadImage(imageUri: Uri, fileName: String, onSuccess: (String) -> Unit, onError: (Throwable) -> Unit) {
        val storageRef = FirebaseStorage.getInstance()
            .reference
            .child(fileName)

        storageRef.putFile(imageUri)
            .addOnProgressListener { snap ->
                val percent = 100.0 * snap.bytesTransferred / snap.totalByteCount
                Log.d(LOG_TAG, "Uploading: ${percent.toInt()}%")
            }
            .addOnSuccessListener {
                // 3) Once the file is uploaded, get its public download URL
                storageRef.downloadUrl.addOnSuccessListener { downloadUri ->
                    Log.d(LOG_TAG, "Retrieved download url: $downloadUri")
                    onSuccess(downloadUri.toString())
                }
            }
            .addOnFailureListener { e ->
                Log.e(LOG_TAG, "Upload failed", e)
                onError(e)
            }
    }
}