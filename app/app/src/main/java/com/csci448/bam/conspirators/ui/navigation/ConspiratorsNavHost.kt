package com.csci448.bam.conspirators.ui.navigation

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.csci448.bam.conspirators.ui.navigation.specs.IScreenSpec
import com.csci448.bam.conspirators.viewmodel.ConspiratorsViewModel
import java.io.File
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

@Composable
fun ConspiratorsNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    conspiratorsViewModel: ConspiratorsViewModel,
    context: Context,
//    shouldShowCamera: MutableState<Boolean>,
    outputDirectory: File,
//    cameraExecutor: ExecutorService,
//    handleImageCapture: (Uri) -> Unit
) {
    val context = LocalContext.current
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = IScreenSpec.ROOT
    ) {
//        conspiratorsViewModel.requestCameraPermission()
//        outputDirectory = conspiratorsViewModel.getOutputDirectory()
//        cameraExecutor = Executors.newSingleThreadExecutor()
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
                            shouldShowCamera = conspiratorsViewModel.shouldShowCamera,
                            outputDirectory = outputDirectory,
                            cameraExecutor = conspiratorsViewModel.cameraExecutor,
                            handleImageCapture = {uri -> conspiratorsViewModel.handleImageCapture(uri,
                                context
                            )}
                        )
                    }
                }
            }
        }
    }
}