package com.csci448.bam.conspirators.data.firestore

import com.csci448.bam.conspirators.data.converters.ConnectionFB
import java.util.UUID

private val nothing = null

data class Board(
    val id: String? = null,
    val userId: String = "",
    val userName: String = "",
    val name: String = "",
    // keys and values here are IDs used in images map
    val connections: List<ConnectionFB?> = listOf(null),
    val images: Map<String, Image> = mapOf(),
    val thumbnailImageUrl: String? = null
    )
