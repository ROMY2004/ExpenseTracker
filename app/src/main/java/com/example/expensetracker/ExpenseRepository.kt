package com.example.expensetracker

import kotlinx.coroutines.flow.Flow

// repositry for handlng data operatons
class ExpenseRepository(private val expenseDao: ExpenseDao) {
    
    // get all expens as flow
    fun getAllExpenses(): Flow<List<Expense>> = expenseDao.getAllExpenses()
    
    // get expens filtred by date
    fun getExpensesByDate(date: String): Flow<List<Expense>> = expenseDao.getExpensesByDate(date)
    
    // insert expens to databse
    suspend fun insertExpense(expense: Expense) {
        expenseDao.insertExpense(expense)
    }
    
    // get toal amout for spesific date
    suspend fun getTotalForDate(date: String): Double {
        return expenseDao.getTotalForDate(date) ?: 0.0
    }
}

