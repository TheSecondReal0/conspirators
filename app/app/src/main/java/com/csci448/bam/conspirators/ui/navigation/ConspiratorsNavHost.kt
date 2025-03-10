package com.csci448.bam.conspirators.ui.navigation

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.csci448.bam.conspirators.ui.navigation.specs.IScreenSpec
import com.csci448.bam.conspirators.viewmodel.ConspiratorsViewModel

@Composable
fun ConspiratorsNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    conspiratorsViewModel: ConspiratorsViewModel,
    context: Context
) {
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = IScreenSpec.ROOT
    ) {
        navigation(
            route = IScreenSpec.ROOT,
            startDestination = IScreenSpec.startDestination
        ) {
            IScreenSpec.allScreens.forEach { (_, screen) ->
                if(screen != null) {
                    composable(
                        route = screen.route,
                        arguments = screen.arguments
                    ) { navBackStackEntry ->
                        screen.Content(
                            modifier = modifier,
                            navController = navController,
                            navBackStackEntry = navBackStackEntry,
                            conspiratorsViewModel = conspiratorsViewModel,
                            context = context
                        )
                    }
                }
            }
        }
    }
}