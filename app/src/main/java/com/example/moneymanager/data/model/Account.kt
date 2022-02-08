package com.example.moneymanager.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Account(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val group: String,
    val name: String,
    val amount: Float,
    val includeInTotals: Boolean
)
