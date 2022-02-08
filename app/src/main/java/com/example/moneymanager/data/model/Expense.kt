package com.example.moneymanager.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.sql.Timestamp

@Entity
data class Expense(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val date: Timestamp,
    val account: Account,
    val amount: Float,
    val description: String,
    val imagePath: String
)
