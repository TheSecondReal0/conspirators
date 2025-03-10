package com.csci448.bam.conspirators.ui.navigation.specs

import android.content.Context
import android.util.Log
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import com.csci448.bam.conspirators.viewmodel.ConspiratorsViewModel

sealed interface IScreenSpec {
    companion object {
        private const val LOG_TAG = "448.IScreenSpec"

        val allScreens = IScreenSpec::class.sealedSubclasses.associate {
            Log.d(LOG_TAG, "allScreens: mapping route \"${it.objectInstance?.route ?: ""}\" to object \"${it.objectInstance}\"")
            it.objectInstance?.route to it.objectInstance
        }
        const val ROOT = "conspirators"
        val startDestination = ListScreenSpec.route

        @Composable
        fun TopBar(vm: ConspiratorsViewModel, navController: NavHostController,
                   navBackStackEntry: NavBackStackEntry?, context: Context) {

            val route = navBackStackEntry?.destination?.route ?: ""
            allScreens[route]?.TopAppBarContent(vm, navController, navBackStackEntry, context)
        }
    }

    val title: Int
    val route: String
    val arguments: List<NamedNavArgument>
    fun buildRoute(vararg args: String?): String

    @Composable
    fun Content(
        modifier: Modifier,
        conspiratorsViewModel: ConspiratorsViewModel,
        navController: NavHostController,
        navBackStackEntry: NavBackStackEntry,
        context: Context
    )

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun TopAppBarContent(vm: ConspiratorsViewModel, navController: NavHostController,
                         navBackStackEntry: NavBackStackEntry?, context: Context) {

//        TopAppBar(
//            title = {
//                Text(stringResource(title))
//            },
//            navigationIcon =
//                if (navController.previousBackStackEntry != null) {
//                    {
//                        IconButton(onClick = { navController.navigateUp() }) {
//                            Icon(
//                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
//                                contentDescription = stringResource(id = R.string.menu_back_desc)
//                            )
//                        }
//                    }
//                } else {
//                    { }
//                },
//            actions = { TopAppBarActions(vm, navController, navBackStackEntry, context) },
//
//        )
    }

    @Composable
    fun TopAppBarActions(vm: ConspiratorsViewModel, navController: NavHostController, navBackStackEntry: NavBackStackEntry?, context: Context)
}