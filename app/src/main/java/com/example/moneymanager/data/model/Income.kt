package com.example.moneymanager.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(foreignKeys = [ForeignKey(
    entity = Account::class,
    onDelete = ForeignKey.CASCADE,
    parentColumns = ["id"],
    childColumns = ["accountId"])])
data class Income(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val date: Long,
    val accountId: Long,
    val amount: Float,
    val description: String,
    val imagePath: String
)
