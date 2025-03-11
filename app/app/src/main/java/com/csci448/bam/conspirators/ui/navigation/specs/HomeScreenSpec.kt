package com.csci448.bam.conspirators.ui.navigation.specs

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import com.csci448.bam.conspirators.R
import com.csci448.bam.conspirators.ui.home.HomeScreen
import com.csci448.bam.conspirators.ui.list.ListScreen
import com.csci448.bam.conspirators.viewmodel.ConspiratorsViewModel

object HomeScreenSpec: IScreenSpec {
    private const val LOG_TAG = "448.HomeScreenSpec"

    override val title = R.string.app_name

    override val route = "home"
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
        HomeScreen(modifier, conspiratorsViewModel)
    }

    @Composable
    override fun TopAppBarActions(vm: ConspiratorsViewModel, navController: NavHostController,
                                  navBackStackEntry: NavBackStackEntry?, context: Context
    ) {
//        IconButton(onClick = { navController.navigate(BoardScreenSpec.route) }) {
//            Icon(Icons.Filled.AddCircle, contentDescription = stringResource(R.string.menu_add_character_desc))
//        }
    }
}