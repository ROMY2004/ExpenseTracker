package com.example.expensetracker

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

// viewmodl for managng ui stat and data
class ExpenseViewModel(application: Application) : AndroidViewModel(application) {
    
    private val repository: ExpenseRepository
    
    // stat for expens list
    private val _expenses = MutableStateFlow<List<Expense>>(emptyList())
    val expenses: StateFlow<List<Expense>> = _expenses
    
    // stat for filtr mode
    private val _isFiltered = MutableStateFlow(false)
    val isFiltered: StateFlow<Boolean> = _isFiltered
    
    init {
        val expenseDao = ExpenseDatabase.getDatabase(application).expenseDao()
        repository = ExpenseRepository(expenseDao)
        loadAllExpenses()
    }
    
    // lod all expens from databse
    private fun loadAllExpenses() {
        viewModelScope.launch {
            repository.getAllExpenses().collect { expenseList ->
                _expenses.value = expenseList
                _isFiltered.value = false
            }
        }
    }
    
    // ad new expens to databse
    fun addExpense(name: String, amount: Double, date: String) {
        viewModelScope.launch {
            val expense = Expense(name = name, amount = amount, date = date)
            repository.insertExpense(expense)
        }
    }
    
    // filtr expens by date
    fun filterByDate(date: String) {
        viewModelScope.launch {
            repository.getExpensesByDate(date).collect { expenseList ->
                _expenses.value = expenseList
                _isFiltered.value = true
            }
        }
    }
    
    // show all expens witout filtr
    fun showAllExpenses() {
        loadAllExpenses()
    }
    
    // get toal amout for spesific date
    suspend fun getTotalForDate(date: String): Double {
        return repository.getTotalForDate(date)
    }
}

