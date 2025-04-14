package com.csci448.bam.conspirators.data.firestore

data class Board(
    val id: String = "",
    val userId: String = "",
    val name: String = "",
    val connections: List<Connection> = listOf(),
    val images: List<Image> = listOf(),
    )
