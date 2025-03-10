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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.csci448.bam.conspirators.ui.navigation.ConspiratorsNavHost
import com.csci448.bam.conspirators.ui.theme.ConspiratorsTheme
import com.csci448.bam.conspirators.viewmodel.ConspiratorsViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ConspiratorsTheme{
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    val navController = rememberNavController()
                    val context = LocalContext.current
                    ConspiratorsNavHost(
                        Modifier.padding(innerPadding),
                        navController,
                        ConspiratorsViewModel(),
                        context,
                    )
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
            val navController = rememberNavController()
            val context = LocalContext.current
            ConspiratorsNavHost(
                Modifier.padding(innerPadding),
                navController,
                ConspiratorsViewModel(),
                context,
            )
//            BoardScreen(modifier = Modifier.padding(innerPadding), viewModel = DrawingViewModel())
        }
    }
}