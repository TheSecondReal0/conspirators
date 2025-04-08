package com.csci448.bam.conspirators.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity("users")
data class User(
    var userName: String,
    @PrimaryKey var userId: UUID,
    var profilePicId: Int) {

}