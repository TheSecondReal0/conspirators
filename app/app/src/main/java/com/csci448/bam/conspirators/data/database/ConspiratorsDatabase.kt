package com.csci448.bam.conspirators.data.database

import androidx.room.Database
import com.csci448.bam.conspirators.data.Board

@Database(entities = [Board::class], version = 1)
abstract class ConspiratorsDatabase {
}