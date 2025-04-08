package com.csci448.bam.conspirators.ui.navigation.specs

import android.app.Activity
import android.app.Activity.RESULT_OK
import android.content.Context
import android.util.Log
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import com.csci448.bam.conspirators.MainActivity
import com.csci448.bam.conspirators.R
import com.csci448.bam.conspirators.ui.list.ListScreen
import com.csci448.bam.conspirators.ui.profile.ProfileScreen
import com.csci448.bam.conspirators.viewmodel.ConspiratorsViewModel
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

data object ProfileScreenSpec : IScreenSpec{
    private const val LOG_TAG = "448.ListScreenSpec"

    override val title = R.string.app_name

    override val route = "profile"
    override val arguments: List<NamedNavArgument> = emptyList()
    override fun buildRoute(vararg args: String?) = route

    @Composable
    override fun Content(
        modifier: Modifier,
        conspiratorsViewModel: ConspiratorsViewModel,
        navController: NavHostController,
        navBackStackEntry: NavBackStackEntry,
        context: Context
    ) {
        ProfileScreen(
            modifier, conspiratorsViewModel,
            signInClicked = {

            },
        )
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