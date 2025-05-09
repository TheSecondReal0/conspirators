package com.csci448.bam.conspirators.ui.navigation.specs

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import com.csci448.bam.conspirators.viewmodel.ConspiratorsViewModel
import java.io.File
import java.util.UUID
import java.util.concurrent.ExecutorService

sealed interface IScreenSpec {
    companion object {
        private const val LOG_TAG = "448.IScreenSpec"

        val allScreens = IScreenSpec::class.sealedSubclasses.associate {
            Log.d(LOG_TAG, "allScreens: mapping route \"${it.objectInstance?.route ?: ""}\" to object \"${it.objectInstance}\"")
            it.objectInstance?.route to it.objectInstance
        }
        const val ROOT = "conspirators"
        val startDestination = HomeScreenSpec.route

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
        context: Context,
        shouldShowCamera: MutableState<Boolean>,
        outputDirectory: File,
        cameraExecutor: ExecutorService,
        handleImageCapture: (Uri) -> Unit
    )

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun TopAppBarContent(vm: ConspiratorsViewModel, navController: NavHostController,
                         navBackStackEntry: NavBackStackEntry?, context: Context) {
        Row (Modifier.consumeWindowInsets(PaddingValues(all = 5.dp)).fillMaxHeight(0.1f).fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            TopAppBarActions(vm, navController, navBackStackEntry, context)
        }
    }

    @Composable
    fun TopAppBarActions(vm: ConspiratorsViewModel, navController: NavHostController,
                                  navBackStackEntry: NavBackStackEntry?, context: Context) {
        Log.i("ALKJLKDSJFL", "TOP APP WAS MADE IN HOME SCREEN")
        IconButton(onClick = {navController.navigate(ListScreenSpec.route)}) {
            Icon(Icons.Filled.Search, contentDescription = "Explore")
        }
        IconButton(onClick = {
            vm.currentThumbnailImage.value = null
            vm.currentBoardTitle.value = "My Board"
            navController.navigate(BoardCreateScreenSpec.route) }) {
            Icon(Icons.Filled.AddCircle, contentDescription = "New Board")
        }
        IconButton(onClick = { navController.navigate(HomeScreenSpec.route)}) {
            vm.refreshLocalScreenWithFirBaseData()
            Icon(Icons.Filled.Home, contentDescription = "Home")
        }
//        // TODO delete this
//        IconButton(onClick = { navController.navigate(WeirdAsaTestScreenSpec.route) }) {
//            Icon(Icons.Filled.Warning, contentDescription = "TEST BUTTON")
//        }
    }
}