package com.csci448.bam.conspirators.ui.navigation.specs

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import com.csci448.bam.conspirators.R
import com.csci448.bam.conspirators.ui.list.ListScreen
import com.csci448.bam.conspirators.viewmodel.ConspiratorsViewModel
import java.io.File
import java.util.concurrent.ExecutorService

data object ListScreenSpec : IScreenSpec{
    private const val LOG_TAG = "448.ListScreenSpec"

    override val title = R.string.app_name

    override val route = "list"
    override val arguments: List<NamedNavArgument> = emptyList()
    override fun buildRoute(vararg args: String?) = route

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
        ListScreen(modifier, conspiratorsViewModel)
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