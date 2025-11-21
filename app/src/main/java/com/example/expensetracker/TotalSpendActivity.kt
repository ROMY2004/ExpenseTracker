package com.example.expensetracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.lifecycleScope
import com.example.expensetracker.ui.theme.ExpenseTrackerTheme
import kotlinx.coroutines.launch

// activty to show toal spend for selectd date
class TotalSpendActivity : ComponentActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // get date from intent
        val selectedDate = intent.getStringExtra("SELECTED_DATE") ?: ""
        
        setContent {
            ExpenseTrackerTheme {
                TotalSpendScreen(selectedDate)
            }
        }
    }
    
    @Composable
    fun TotalSpendScreen(date: String) {
        val viewModel = remember { ExpenseViewModel(application) }
        var totalAmount by remember { mutableStateOf(0.0) }
        
        // calcuate toal when screen loads
        LaunchedEffect(date) {
            lifecycleScope.launch {
                totalAmount = viewModel.getTotalForDate(date)
            }
        }
        
        // simpel ui to dispay toal
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF5F5F5))
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Total Spend on $date:",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = totalAmount.toString(),
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF6750A4)
                )
            }
        }
    }
}

