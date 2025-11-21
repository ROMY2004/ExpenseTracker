package com.example.expensetracker

import androidx.room.Entity
import androidx.room.PrimaryKey

// entitiy for storing expence data in databse
@Entity(tableName = "expenses")
data class Expense(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,  // expens name or descriptin
    val amount: Double,  // amout of expens
    val date: String  // dat when expens was mde
)

