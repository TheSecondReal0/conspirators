package com.csci448.bam.conspirators.data.firestore

data class Board(
    val id: String = "",
    val userId: String = "",
    val name: String = "",
    // keys and values here are IDs used in images map
    val connections: Map<String, String> = mapOf(),
    val images: Map<String, Image> = mapOf(),
    )
