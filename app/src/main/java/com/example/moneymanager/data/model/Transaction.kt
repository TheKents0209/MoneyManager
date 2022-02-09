package com.example.moneymanager.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(tableName = "transactions", foreignKeys = [ForeignKey(
    entity = Account::class,
    onDelete = ForeignKey.CASCADE,
    parentColumns = ["id"],
    childColumns = ["accountId"])])
data class Transaction(
    @PrimaryKey(autoGenerate = true)
    val transactionId: Long,
    //-1 for expense, 0 for transaction, +1 for income
    val type: Int,
    val date: String,
    val accountId: Long,
    val amount: Float,
    val description: String,
    val imagePath: String
)