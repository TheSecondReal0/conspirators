package com.csci448.bam.conspirators

import android.app.Activity.RESULT_OK
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.csci448.bam.conspirators.data.BoardRepo
import com.csci448.bam.conspirators.data.UserRepo
import com.csci448.bam.conspirators.ui.navigation.ConspiratorsNavHost
import com.csci448.bam.conspirators.ui.navigation.ConspiratorsTopBar
import com.csci448.bam.conspirators.ui.theme.ConspiratorsTheme
import com.csci448.bam.conspirators.viewmodel.ConspiratorsViewModel
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.firebase.Firebase
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth

class MainActivity : ComponentActivity() {

    companion object {
        val LOG_TAG: String = "Main"
    }
    // This is all stuff for firebase login
    private lateinit var auth: FirebaseAuth


    val conspiratorsViewModel = ConspiratorsViewModel(BoardRepo.boards, UserRepo.users)


    // Create and launch sign-in intent



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ConspiratorsTheme{
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    val navController = rememberNavController()
                    val context = LocalContext.current

                    Box(contentAlignment = Alignment.BottomCenter) {
                        ConspiratorsNavHost(
                            Modifier.padding(innerPadding),
                            navController,
                            conspiratorsViewModel,
                            context,
                        )
                        ConspiratorsTopBar(navController, conspiratorsViewModel, context)
                    }
                }
            }
        }
    }




    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        /*
        val currentUser = auth.currentUser
        if (currentUser != null) {
            conspiratorsViewModel.setUser(currentUser)
            //reload() this will reload the screen for the current user
        }
        else {
            // now we need to handle if they got logged out
        }

         */
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
                ConspiratorsViewModel(BoardRepo.boards, UserRepo.users),
                context,
            )
//            BoardScreen(modifier = Modifier.padding(innerPadding), viewModel = DrawingViewModel())
        }
    }
}