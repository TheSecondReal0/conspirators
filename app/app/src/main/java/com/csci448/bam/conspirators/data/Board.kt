package com.csci448.bam.conspirators.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity("boards")
data class Board(
    @PrimaryKey val boardUUID: UUID,
    var thumbnailImgId: Int,
    var name: String,
    val userUUID: UUID) {

}