package com.example.expensetracker

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.expensetracker.ui.theme.ExpenseTrackerTheme
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ExpenseTrackerTheme {
                ExpenseTrackerScreen(
                    onExpenseClick = { date ->
                        // open toal spend activty when expens clicked
                        val intent = Intent(this, TotalSpendActivity::class.java)
                        intent.putExtra("SELECTED_DATE", date)
                        startActivity(intent)
                    }
                )
            }
        }
    }
}

@Composable
fun ExpenseTrackerScreen(onExpenseClick: (String) -> Unit) {
    val vm: ExpenseViewModel = viewModel()
    val expenses by vm.expenses.collectAsState()
    
    // stat for inpt fields
    var expenName by remember { mutableStateOf("") }
    var expenAmount by remember { mutableStateOf("") }
    
    // stat for date selctr
    var selectedDate by remember { mutableStateOf(getCurrentDate()) }
    //var showCalendar by remember { mutableStateOf(false) }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
            .padding(16.dp)
    ) {
        // inpt field for expens name
        OutlinedTextField(
            value = expenName,
            onValueChange = { expenName = it },
            label = { Text("Enter Expense Name") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFFE91E63),
                unfocusedBorderColor = Color.Gray
            )
        )
        
        // inpt field for expens amout
        OutlinedTextField(
            value = expenAmount,
            onValueChange = { expenAmount = it },
            label = { Text("Enter Expense Amount") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color.Gray,
                unfocusedBorderColor = Color.Gray
            )
        )
        
        // date displayer
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF5F5F6F), RoundedCornerShape(4.dp))
                .padding(16.dp)
        ) {
            Column {
                Text(
                    text = getYearFromDate(selectedDate),
                    color = Color.White,
                    fontSize = 14.sp
                )
                Text(
                    text = formatDateDisplay(selectedDate),
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // calendr picker
        CalendarView(
            selectedDate = selectedDate,
            onDateSelected = { date ->
                selectedDate = date
            }
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // butons row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            
            // filtr buton
            Button(
                onClick = {
                    vm.filterByDate(selectedDate)
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6750A4)),
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(24.dp)
            ) {
                Text("Filter", fontSize = 16.sp)
            }

            // add buton
            Button(
                onClick = {
                    if (expenName.isNotBlank() && expenAmount.isNotBlank()) {
                        val amountofexpense = expenAmount.toDoubleOrNull() ?: 0.0
                        vm.addExpense(expenName, amountofexpense, selectedDate)
                        expenAmount = ""
                        expenName = ""
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6750A4)),
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(24.dp)
            ) {
                Text("Add", fontSize = 16.sp)
            }

            // showall buton
            Button(
                onClick = {
                    vm.showAllExpenses()
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6750A4)),
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(24.dp)
            ) {
                Text("Show All", fontSize = 16.sp)
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // expens list
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(expenses) { expense ->
                ExpenseItem(
                    expense = expense,
                    onClick = { onExpenseClick(expense.date) }
                )
            }
        }
    }
}

@Composable
fun ExpenseItem(expense: Expense, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White, RoundedCornerShape(4.dp))
            .clickable { onClick() }
            .padding(16.dp)
    ) {
        Text(
            text = "${expense.name} - ${expense.amount} (${expense.date})",
            fontSize = 16.sp,
            color = Color.Black
        )
    }
}

@Composable
fun CalendarView(
    selectedDate: String,
    onDateSelected: (String) -> Unit
) {
    val calendar = Calendar.getInstance()
    val currentMonth = calendar.get(Calendar.MONTH)
    val currentYear = calendar.get(Calendar.YEAR)
    
    var displayedMonth by remember { mutableStateOf(currentMonth) }
    var displayedYear by remember { mutableStateOf(currentYear) }
    
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White, RoundedCornerShape(8.dp))
            .padding(16.dp)
    ) {
        // month navigaton header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = {
                if (displayedMonth == 0) {
                    displayedMonth = 11
                    displayedYear -= 1
                } else {
                    displayedMonth -= 1
                }
            }) {
                Text("<", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            }
            
            Text(
                text = "${getMonthName(displayedMonth)} $displayedYear",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            
            IconButton(onClick = {
                if (displayedMonth == 11) {
                    displayedMonth = 0
                    displayedYear += 1
                } else {
                    displayedMonth += 1
                }
            }) {
                Text(">", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            }
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // day headers
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            listOf("S", "M", "T", "W", "T", "F", "S").forEach { day ->
                Text(
                    text = day,
                    fontSize = 12.sp,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    color = Color.Gray
                )
            }
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // calendr grid
        val daysInMonth = getDaysInMonth(displayedMonth, displayedYear)
        val firstDayOfWeek = getFirstDayOfWeek(displayedMonth, displayedYear)
        
        var dayCounter = 1
        for (week in 0..5) {
            if (dayCounter > daysInMonth) break
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                for (dayOfWeek in 0..6) {
                    val currentDay = if (week == 0 && dayOfWeek < firstDayOfWeek) {
                        0
                    } else if (dayCounter > daysInMonth) {
                        0
                    } else {
                        dayCounter++
                    }
                    
                    if (currentDay > 0) {
                        val dateString = formatDate(currentDay, displayedMonth, displayedYear)
                        val isSelected = dateString == selectedDate
                        
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1f)
                                .padding(2.dp)
                                .background(
                                    if (isSelected) Color(0xFF6750A4) else Color.Transparent,
                                    RoundedCornerShape(50)
                                )
                                .clickable { onDateSelected(dateString) },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = currentDay.toString(),
                                fontSize = 14.sp,
                                color = if (isSelected) Color.White else Color.Black
                            )
                        }
                    } else {
                        Spacer(modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f))
                    }
                }
            }
        }
    }
}

// helper funtion to get curent date
fun getCurrentDate(): String {
    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    return sdf.format(Date())
}

// helper funtion to format date
fun formatDate(day: Int, month: Int, year: Int): String {
    return String.format("%04d-%02d-%02d", year, month + 1, day)
}

// helper funtion to get days in month
fun getDaysInMonth(month: Int, year: Int): Int {
    val calendar = Calendar.getInstance()
    calendar.set(Calendar.YEAR, year)
    calendar.set(Calendar.MONTH, month)
    return calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
}

// helper funtion to get first day of weak
fun getFirstDayOfWeek(month: Int, year: Int): Int {
    val calendar = Calendar.getInstance()
    calendar.set(Calendar.YEAR, year)
    calendar.set(Calendar.MONTH, month)
    calendar.set(Calendar.DAY_OF_MONTH, 1)
    return calendar.get(Calendar.DAY_OF_WEEK) - 1
}

// helper funtion to get month nam
fun getMonthName(month: Int): String {
    val months = listOf(
        "January", "February", "March", "April", "May", "June",
        "July", "August", "September", "October", "November", "December"
    )
    return months[month]
}

// helper funtion to format date for dispay
fun formatDateDisplay(date: String): String {
    try {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val parsedDate = sdf.parse(date) ?: return date
        val displayFormat = SimpleDateFormat("EEE, MMM dd", Locale.getDefault())
        return displayFormat.format(parsedDate)
    } catch (e: Exception) {
        return date
    }
}

// helper funtion to get yer from date
fun getYearFromDate(date: String): String {
    return date.substring(0, 4)
}
