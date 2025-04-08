package com.csci448.bam.conspirators

import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.compose.rememberNavController
import com.csci448.bam.conspirators.data.BoardRepo
import com.csci448.bam.conspirators.data.UserRepo
import com.csci448.bam.conspirators.ui.navigation.ConspiratorsNavHost
import com.csci448.bam.conspirators.ui.navigation.ConspiratorsTopBar
import com.csci448.bam.conspirators.ui.theme.ConspiratorsTheme
import com.csci448.bam.conspirators.viewmodel.ConspiratorsViewModel
import com.google.firebase.auth.FirebaseAuth
import android.Manifest
import android.annotation.SuppressLint
import android.net.Uri
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import java.io.File
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class MainActivity : ComponentActivity() {

    companion object {
        val LOG_TAG: String = "Main"
    }
    // This is all stuff for firebase login
    private lateinit var auth: FirebaseAuth


    val conspiratorsViewModel = ConspiratorsViewModel(BoardRepo.boards, UserRepo.users)


    // Create and launch sign-in intent



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ConspiratorsTheme{
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    val navController = rememberNavController()
                    val context = LocalContext.current

                    Box(contentAlignment = Alignment.BottomCenter) {
                        ConspiratorsNavHost(
                            Modifier.padding(innerPadding),
                            navController,
                            conspiratorsViewModel,
                            context,
                            shouldShowCamera,
                            outputDirectory,
                            cameraExecutor,

                            ) { uri: Uri -> handleImageCapture(uri) }
                        ConspiratorsTopBar(navController, conspiratorsViewModel, context)
                    }
                }
            }
        }
        requestCameraPermission()

        outputDirectory = getOutputDirectory()
        cameraExecutor = Executors.newSingleThreadExecutor()
    }




    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        /*
        val currentUser = auth.currentUser
        if (currentUser != null) {
            conspiratorsViewModel.setUser(currentUser)
            //reload() this will reload the screen for the current user
        }
        else {
            // now we need to handle if they got logged out
        }

         */
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            Log.i("Camera", "Permission granted")
        } else {
            Log.i("Camera", "Permission denied")
        }
    }

    private fun requestCameraPermission() {
        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED -> {
                Log.i("Camera", "Permission previously granted")
            }

            ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                Manifest.permission.CAMERA
            ) -> Log.i("Camera", "Show camera permissions dialog")

            else -> requestPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    private lateinit var outputDirectory: File
    private lateinit var cameraExecutor: ExecutorService
    private var shouldShowCamera: MutableState<Boolean> = mutableStateOf(false)
    private lateinit var photoUri: Uri
    private var shouldShowPhoto: MutableState<Boolean> = mutableStateOf(false)

    fun handleImageCapture(uri: Uri) {
        Log.i("kilo", "Image captured: $uri")
        shouldShowCamera.value = false
        photoUri = uri
        shouldShowPhoto.value = true
    }

    private fun getOutputDirectory(): File {
        val mediaDir = externalMediaDirs.firstOrNull()?.let {
            File(it, resources.getString(R.string.app_name)).apply { mkdirs() }
        }

        return if (mediaDir != null && mediaDir.exists()) mediaDir else filesDir
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }
}




@SuppressLint("UnrememberedMutableState")
@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    val shouldShowCamera: MutableState<Boolean> = mutableStateOf(false)
//    ConspiratorsTheme {
//        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
//            val navController = rememberNavController()
//            val context = LocalContext.current
//            ConspiratorsNavHost(
//                Modifier.padding(innerPadding),
//                navController,
//                ConspiratorsViewModel(BoardRepo.boards, UserRepo.users),
//                context,
//                shouldShowCamera,
//                outputDirectory,
//                cameraExecutor,
//                handleImageCapture,
//            )
////            BoardScreen(modifier = Modifier.padding(innerPadding), viewModel = DrawingViewModel())
//        }
//    }
}