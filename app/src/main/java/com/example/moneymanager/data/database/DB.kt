package com.example.moneymanager.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.moneymanager.data.dao.AccountDao
import com.example.moneymanager.data.dao.TransactionDao
import com.example.moneymanager.data.model.Account
import com.example.moneymanager.data.model.Transaction

//Not yet completed, add all models
@Database(entities = [(Transaction::class), (Account::class)], version = 1)
abstract class DB: RoomDatabase() {
    abstract fun TransactionDao(): TransactionDao
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