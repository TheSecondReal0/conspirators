package com.csci448.bam.conspirators.ui.navigation

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.csci448.bam.conspirators.ui.navigation.specs.IScreenSpec
import com.csci448.bam.conspirators.viewmodel.ConspiratorsViewModel
import java.io.File
import java.util.concurrent.ExecutorService

@Composable
fun ConspiratorsNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    conspiratorsViewModel: ConspiratorsViewModel,
    context: Context,
    shouldShowCamera: MutableState<Boolean>,
    outputDirectory: File,
    cameraExecutor: ExecutorService,
    handleImageCapture: (Uri) -> Unit
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
                            context = context,
                            shouldShowCamera = shouldShowCamera,
                            outputDirectory = outputDirectory,
                            cameraExecutor = cameraExecutor,
                            handleImageCapture = handleImageCapture

                        )
                    }
                }
            }
        }
    }
}