package com.csci448.bam.conspirators.ui.navigation.specs

import android.app.Activity.RESULT_OK
import android.content.Context
import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import com.csci448.bam.conspirators.MainActivity
import com.csci448.bam.conspirators.R
import com.csci448.bam.conspirators.ui.profile.ProfileScreen
import com.csci448.bam.conspirators.viewmodel.ConspiratorsViewModel
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import java.io.File
import java.util.concurrent.ExecutorService

data object ProfileScreenSpec : IScreenSpec{
    private const val LOG_TAG = "448.ListScreenSpec"

    override val title = R.string.app_name

    override val route = "profile"
    override val arguments: List<NamedNavArgument> = emptyList()
    override fun buildRoute(vararg args: String?) = route
    // Choose authentication providers
    val providers = arrayListOf(
        AuthUI.IdpConfig.EmailBuilder().build()
    )
    @Composable
    override fun Content(
        modifier: Modifier,
        conspiratorsViewModel: ConspiratorsViewModel,
        navController: NavHostController,
        navBackStackEntry: NavBackStackEntry,
        context: Context,
        shouldShowCamera: MutableState<Boolean>,
        outputDirectory: File,
        cameraExecutor: ExecutorService,
        handleImageCapture: (Uri) -> Unit
    ) {
        ProfileScreen(
            modifier, conspiratorsViewModel,
            signInClicked = {
                val signInIntent = AuthUI.getInstance()
                    .createSignInIntentBuilder()
                    .setAvailableProviders(providers)
                    .build()

                val signInLauncher = MainActivity().registerForActivityResult(
                    FirebaseAuthUIActivityResultContract(),
                ) { res ->
                    this.onSignInResult(res, viewModel = conspiratorsViewModel)
                }

                signInLauncher.launch(signInIntent)
            },
        )
    }
    var user: FirebaseUser? = null

    private fun onSignInResult(result: FirebaseAuthUIAuthenticationResult, viewModel: ConspiratorsViewModel) {
        val response = result.idpResponse
        if (result.resultCode == RESULT_OK) {
            // Successfully signed in
            user = FirebaseAuth.getInstance().currentUser
            user?.let { viewModel.setUser(it) }
            // ...
        } else {
            // Sign in failed. If response is null the user canceled the
            // sign-in flow using the back button. Otherwise check
            // response.getError().getErrorCode() and handle the error.
            // ...
        }
    }

//    @Composable
//    override fun TopAppBarActions(vm: ConspiratorsViewModel, navController: NavHostController,
//                                  navBackStackEntry: NavBackStackEntry?, context: Context) {
//        Log.i("ALKJLKDSJFL", "TOP APP WAS MADE IN HOME SCREEN")
//        IconButton(onClick = {navController.navigate(ListScreenSpec.route)}) {
//            Icon(Icons.Filled.Search, contentDescription = "Explore")
//        }
//        IconButton(onClick = { navController.navigate(BoardScreenSpec.route) }) {
//            Icon(Icons.Filled.AddCircle, contentDescription = "New Board")
//        }
//        IconButton(onClick = { navController.navigate(HomeScreenSpec.route)}) {
//            Icon(Icons.Filled.Home, contentDescription = "Home")
//        }
//        IconButton(onClick = {navController.navigate(ProfileScreenSpec.route)}) {
//            Icon(Icons.Filled.AccountCircle, contentDescription = "Home")
//        }
//    }
}