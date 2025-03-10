package com.csci448.bam.conspirators.ui.navigation

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.csci448.bam.conspirators.ui.navigation.specs.IScreenSpec
import com.csci448.bam.conspirators.viewmodel.ConspiratorsViewModel

@Composable
fun ConspiratorsTopBar(navController: NavHostController, conspiratorsViewModel: ConspiratorsViewModel,
                       context: Context) {

    val navBackStackEntryState = navController.currentBackStackEntryAsState()
    IScreenSpec.TopBar(conspiratorsViewModel, navController, navBackStackEntryState.value, context)
}