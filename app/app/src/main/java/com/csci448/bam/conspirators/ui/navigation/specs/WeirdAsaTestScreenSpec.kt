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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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

data object WeirdAsaTestScreenSpec : IScreenSpec {
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
        var fullRoute = "weird"
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
        var testBoard by remember { mutableStateOf(Board(name = "test_fella")) }

        val board: Board? = conspiratorsViewModel.board

        Column(
            modifier = modifier
        ) {
            if (board == null) {
                Text("null board")
            } else {
                Text("Board ID: ${board.id}")
                Text("User ID: ${board.userId}")
                Text("Board Name: ${board.name}")
            }

            PickAndUploadImage(
                conspiratorsViewModel = conspiratorsViewModel,
                fileName = "cool_fella"
            )

            Button(onClick = {
                conspiratorsViewModel.saveBoard(
                    Board(name = "test_fella"),
                    onSuccess = {
                        Toast.makeText(context, "Test board saved successfully: $it", Toast.LENGTH_LONG).show()
                        Log.d(LOG_TAG, "Test board saved: $it")
                        testBoard = it
                    },
                    onError = {Toast.makeText(context, "Failed to save board: $it", Toast.LENGTH_LONG).show()})
            }) {
                Text("Save test board")
            }

            Button(onClick = {
                conspiratorsViewModel.updateBoard(
                    testBoard.copy(name = "test_fella_updated"),
                    onResult = {
                        Toast.makeText(context, "Test board updated: $it", Toast.LENGTH_LONG).show()
                        Log.d(LOG_TAG, "Test board updated: $it")
                    })
            }) {
                Text("Update test board")
            }

            Button(onClick = {
                conspiratorsViewModel.deleteBoard(
                    testBoard.id ?: "fake_id_shouldnt_be_used",
                    onResult = {
                        Toast.makeText(context, "Test board deleted: $it", Toast.LENGTH_LONG).show()
                        Log.d(LOG_TAG, "Test board deleted: $it")
                    })
            }) {
                Text("Delete test board")
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
