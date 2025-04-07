package com.csci448.bam.conspirators.ui.navigation.specs

import android.content.Context
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import com.csci448.bam.conspirators.MainActivity
import com.csci448.bam.conspirators.R
import com.csci448.bam.conspirators.ui.board.BoardScreen
import com.csci448.bam.conspirators.viewmodel.ConspiratorsViewModel

//import com.csci448.bam.conspirators.components.BoardScreen

data object BoardScreenSpec : IScreenSpec {
    private const val LOG_TAG = "448.BoardScreenSpec"

    override val title = R.string.app_name

    override val route = "board"
    override val arguments: List<NamedNavArgument> = emptyList()
    override fun buildRoute(vararg args: String?): String = route

    @Composable
    override fun Content(
        modifier: Modifier,
        conspiratorsViewModel: ConspiratorsViewModel,
        navController: NavHostController,
        navBackStackEntry: NavBackStackEntry,
        context: Context
    ) {
        BoardScreen(conspiratorsViewModel, modifier, homeClicked = {navController.navigate(HomeScreenSpec.route)})
    }

//    @Composable
//    override fun TopAppBarActions(vm: ConspiratorsViewModel, navController: NavHostController,
//                                  navBackStackEntry: NavBackStackEntry?, context: Context) {
//        IconButton(onClick = { navController.navigate(BoardScreenSpec.route) }) {
//            Icon(Icons.Filled.AddCircle, contentDescription = "New Board")
//        }
//    }
}
