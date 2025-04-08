package com.csci448.bam.conspirators.ui.profile

import android.app.Activity
import android.content.Context
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.csci448.bam.conspirators.R
import com.csci448.bam.conspirators.viewmodel.ConspiratorsViewModel
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract

@Composable
fun ProfileScreen(modifier: Modifier, conspiratorsViewModel: ConspiratorsViewModel, signInClicked: () -> Unit) {
    Column(modifier = modifier) {
        Button(
            onClick = {
                signInClicked()
            },
            modifier = Modifier.align(Alignment.End)
        ) {

        }
        Spacer(modifier = Modifier.height(16.dp))
        Row {
            Column(modifier = Modifier.padding(5.dp)) {

                Image(
                    painter = painterResource(id = conspiratorsViewModel.currentUser.value.profilePicId),
                    contentDescription = "Profile Picture",
                    modifier = Modifier
                        .size(100.dp)
                        .background(Color.Gray, shape = CircleShape)
                        .clip(shape = CircleShape)
                )
            }
            Column(modifier = Modifier
                .padding(10.dp)
                .align(Alignment.CenterVertically)) {
                conspiratorsViewModel.thisUser?.displayName?.let { Text(text = it, fontSize = 30.sp, fontWeight = FontWeight.Bold) }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            ProfileStat(count = "150", label = "Posts")
            ProfileStat(count = "2.3K", label = "Followers")
            ProfileStat(count = "500", label = "Following")
        }
        Spacer(modifier = Modifier.height(16.dp))
        PostGrid(conspiratorsViewModel)
    }
}