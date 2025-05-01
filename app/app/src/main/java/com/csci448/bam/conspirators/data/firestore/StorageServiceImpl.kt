package com.csci448.bam.conspirators.data.firestore

import android.net.Uri
import android.util.Log
import com.csci448.bam.conspirators.data.converters.ConnectionFB
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Filter
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


    override fun getAllBoards(onSuccess: (Map<String, Board>) -> Unit, onError: (Throwable) -> Unit) {
        val docRef = Firebase.firestore.collection("boards")
        docRef.get().addOnSuccessListener {
            Log.d(LOG_TAG, "Successfully retrieved all boards: $it")
            val boards: Map<String, Board> = it.documents.associate { doc -> Pair(doc.id, docToBoard(doc)) }
            onSuccess(boards)
        } .addOnFailureListener {
            Log.e(LOG_TAG, "getAllBoards failed with error $it")
            onError(it)
        }
    }

//TODO uncomment this after nuking old database
/*
private fun docToBoard(doc: DocumentSnapshot): Board {
    var obj: Board? = doc.toObject(Board::class.java)
    if (obj == null) return Board()
    obj = obj!!.copy(
        id = doc.id
    )
    return obj
}

 */
//TODO delete this after the nuke
    private fun docToBoard(doc: DocumentSnapshot): Board {
        val id = doc.id
        val userId = doc.getString("userId") ?: ""
        val name = doc.getString("name") ?: ""

        val imageMap = doc.get("images") as? Map<String, Map<String, Any>> ?: emptyMap()
        val images = imageMap.mapValues { (_, value) ->
            Image(
                imageUrl = value["imageUrl"] as? String ?: "",
                x = (value["x"] as? Number)?.toDouble() ?: 0.0,
                y = (value["y"] as? Number)?.toDouble() ?: 0.0
            )
        }

        val connectionsRaw = doc.get("connections")
        val connections = when (connectionsRaw) {
            is List<*> -> connectionsRaw.mapNotNull { item ->
                val map = item as? Map<*, *> ?: return@mapNotNull null
                ConnectionFB(
                    componentId1 = map["componentId1"] as? String ?: "",
                    componentId2 = map["componentId2"] as? String ?: "",
                    label = map["label"] as? String ?: ""
                )
            }

            is Map<*, *> -> {
                // handle old map format (e.g., {"id1": "id2"})
                connectionsRaw.mapNotNull { (k, v) ->
                    if (k is String && v is String) {
                        ConnectionFB(k, v)
                    } else null
                }
            }

            else -> emptyList()
        }
        return Board(
            id = id,
            userId = userId,
            name = name,
            images = images,
            connections = connections
        )
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