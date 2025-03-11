package com.csci448.bam.conspirators.ui.navigation

import android.content.Context
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.csci448.bam.conspirators.ui.navigation.specs.IScreenSpec
import com.csci448.bam.conspirators.viewmodel.ConspiratorsViewModel

@Composable
fun ConspiratorsTopBar(navController: NavHostController, conspiratorsViewModel: ConspiratorsViewModel,
                       context: Context) {
    //val navBackStackEntryState = navController.currentBackStackEntryAsState()
    //IScreenSpec.TopBar(conspiratorsViewModel, navController, navBackStackEntryState.value, context)

    val navBackStackEntryState = navController.currentBackStackEntryAsState()
    if (navBackStackEntryState.value != null) {
        IScreenSpec.TopBar(
            vm = conspiratorsViewModel,
            navController = navController,
            navBackStackEntry = navBackStackEntryState.value!!,
            context = context
        )
        Log.i("YAYYY", "Created ISCREEN Bar")
    }
    else {
        Log.i("ERROR", "top bar nav was nul")
    }
}