package com.csci448.bam.conspirators.ui.navigation.specs

import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.csci448.bam.conspirators.R
import com.csci448.bam.conspirators.data.firestore.Board
import com.csci448.bam.conspirators.ui.board.BoardScreen
import com.csci448.bam.conspirators.viewmodel.ConspiratorsViewModel
import java.io.File
import java.util.concurrent.ExecutorService

//import com.csci448.bam.conspirators.components.BoardScreen

data object BoardScreenSpec : IScreenSpec {
    private const val LOG_TAG = "448.BoardScreenSpec"
    private const val ARG_BOARD_ID_NAME: String = "id"

    override val title = R.string.app_name

    override val route = buildRoute(ARG_BOARD_ID_NAME)
    override val arguments: List<NamedNavArgument> = listOf(
        navArgument(ARG_BOARD_ID_NAME) {
            type = NavType.StringType
        }
    )
    fun buildFullRoute(boardId: String): String {
        var fullRoute = "board"
        val argVal = boardId
        if(argVal == ARG_BOARD_ID_NAME) {
            fullRoute += "/{$argVal}"
            Log.d(LOG_TAG, "Built base route $fullRoute")
        } else {
            fullRoute += "/$argVal"
            Log.d(LOG_TAG, "Built specific route $fullRoute")
        }
        return fullRoute
    }
    override fun buildRoute(vararg args: String?): String {
        return buildFullRoute(args[0] ?: "")
    }

    @Composable
    override fun Content(
        modifier: Modifier,
        conspiratorsViewModel: ConspiratorsViewModel,
        navController: NavHostController,
        navBackStackEntry: NavBackStackEntry,
        context: Context,
        shouldShowCamera: MutableState<Boolean>,
        outputDirectory: File,
        cameraExecutor: ExecutorService,
        handleImageCapture: (Uri) -> Unit
    ) {
        val board: Board? = conspiratorsViewModel.board
        if (board == null) {
            Text("null board")
        } else {
            Column {
                Text("Board ID: ${board.id}")
                Text("User ID: ${board.userId}")
                Text("Board Name: ${board.name}")
                PickAndUploadImage(
                    conspiratorsViewModel = conspiratorsViewModel,
                    boardId = board.id!!,
                    fileName = "cool_fella"
                )
            }
        }
        val idStr: String = navBackStackEntry.arguments?.getString(ARG_BOARD_ID_NAME) ?: ""
        Log.d(LOG_TAG, "Retrieved id $idStr from navBackStackEntry")
        conspiratorsViewModel.loadBoard(idStr)
//        BoardScreen(conspiratorsViewModel, modifier, homeClicked = {navController.navigate(HomeScreenSpec.route)}, shouldShowCamera, outputDirectory, cameraExecutor, handleImageCapture)
    }

//    @Composable
//    override fun TopAppBarActions(vm: ConspiratorsViewModel, navController: NavHostController,
//                                  navBackStackEntry: NavBackStackEntry?, context: Context) {
//        IconButton(onClick = { navController.navigate(BoardScreenSpec.route) }) {
//            Icon(Icons.Filled.AddCircle, contentDescription = "New Board")
//        }
//    }


    @Composable
    fun PickAndUploadImage(
        conspiratorsViewModel: ConspiratorsViewModel,
        boardId: String,
        fileName: String = "test_image"
    ) {
        val context = LocalContext.current

        // Set up the gallery picker
        val launcher = rememberLauncherForActivityResult(
            ActivityResultContracts.GetContent()
        ) { uri: Uri? ->
            uri?.let {
                uploadImageToFirebase(
                    conspiratorsViewModel = conspiratorsViewModel,
                    context   = context,
                    imageUri  = it,
                    fileName = fileName,
                )
            }
        }

        Button(onClick = { launcher.launch("image/*") }) {
            Text("Pick & Upload Image")
        }
    }

    private fun uploadImageToFirebase(
        conspiratorsViewModel: ConspiratorsViewModel,
        context: Context,
        imageUri: Uri,
        fileName: String,
    ) {
        conspiratorsViewModel.uploadImage(
            imageUri = imageUri,
            fileName = fileName,
            onSuccess = {
                Toast.makeText(context, "Uploaded!\n$it", Toast.LENGTH_LONG).show()
            },
            onError = { e ->
                Toast.makeText(context, "Upload failed: ${e.message}", Toast.LENGTH_LONG).show()
            }
        )
    }
}
