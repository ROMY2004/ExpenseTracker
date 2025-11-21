package com.example.expensetracker

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

// dao for databse operatons
@Dao
interface ExpenseDao {
    
    // insrt new expens to databse
    @Insert
    suspend fun insertExpense(expense: Expense)
    
    // get all expens from databse
    @Query("SELECT * FROM expenses ORDER BY date DESC")
    fun getAllExpenses(): Flow<List<Expense>>
    
    // filtr expens by specifc date
    @Query("SELECT * FROM expenses WHERE date = :date ORDER BY date DESC")
    fun getExpensesByDate(date: String): Flow<List<Expense>>
    
    // calcuate toal spend for spesific date
    @Query("SELECT SUM(amount) FROM expenses WHERE date = :date")
    suspend fun getTotalForDate(date: String): Double?
}

