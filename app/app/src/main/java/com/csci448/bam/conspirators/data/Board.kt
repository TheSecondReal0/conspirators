package com.csci448.bam.conspirators.data

import androidx.room.Entity
import java.util.UUID

@Entity("boards")
data class Board(
    var thumbnailImgId: Int,
    var name: String,
    val userUUID: UUID) {

}