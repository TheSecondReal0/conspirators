package com.csci448.bam.conspirators.ui.navigation.specs

import android.content.Context
import android.util.Log
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import com.csci448.bam.conspirators.R
import com.csci448.bam.conspirators.ui.list.ListScreen
import com.csci448.bam.conspirators.viewmodel.ConspiratorsViewModel

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
        context: Context
    ) {
        ListScreen(modifier, conspiratorsViewModel)
    }

    @Composable
    override fun TopAppBarActions(vm: ConspiratorsViewModel, navController: NavHostController,
                                  navBackStackEntry: NavBackStackEntry?, context: Context) {
//        IconButton(onClick = { navController.navigate(BoardScreenSpec.route) }) {
//            Icon(Icons.Filled.AddCircle, contentDescription = stringResource(R.string.menu_add_character_desc))
//        }
    }
}