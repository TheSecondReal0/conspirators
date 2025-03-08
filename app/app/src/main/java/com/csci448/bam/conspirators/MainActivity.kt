package com.csci448.bam.conspirators

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.csci448.bam.conspirators.ui.theme.ConspiratorsTheme
import com.csci448.busche.testing.DrawingViewModel.DrawingViewModel
import com.csci448.busche.testing.components.BoardScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ConspiratorsTheme{
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    BoardScreen(modifier = Modifier.padding(innerPadding), viewModel = DrawingViewModel())
                }
            }
        }
    }
}



@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    ConspiratorsTheme {
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
            BoardScreen(modifier = Modifier.padding(innerPadding), viewModel = DrawingViewModel())
        }
    }
}