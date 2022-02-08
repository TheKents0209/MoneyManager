package com.example.moneymanager.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.moneymanager.data.dao.AccountDao
import com.example.moneymanager.data.dao.ExpenseDao
import com.example.moneymanager.data.dao.IncomeDao
import com.example.moneymanager.data.model.Account
import com.example.moneymanager.data.model.Expense
import com.example.moneymanager.data.model.Income

//Not yet completed, add all models
@Database(entities = [(Expense::class), (Income::class), (Account::class)], version = 1)
abstract class DB: RoomDatabase() {
    abstract fun ExpenseDao(): ExpenseDao
    abstract fun IncomeDao(): IncomeDao
    abstract fun AccountDao(): AccountDao

    companion object{
        private var instance: DB? = null
        @Synchronized
        fun getInstance(context: Context): DB {
            if(instance == null) {
                instance = Room.databaseBuilder(context.applicationContext, DB::class.java, "manager.db").build()
            }
            return instance!!
        }
    }
}