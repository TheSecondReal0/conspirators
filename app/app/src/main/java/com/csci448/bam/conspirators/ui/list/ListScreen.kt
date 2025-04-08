package com.csci448.bam.conspirators.ui.list

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.csci448.bam.conspirators.R
import com.csci448.bam.conspirators.ui.sharedComponents.BoardCard
import com.csci448.bam.conspirators.viewmodel.ConspiratorsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListScreen(modifier: Modifier, conspiratorsViewModel: ConspiratorsViewModel) {
    Column(modifier = modifier) {
        val localContext = LocalContext.current
        SearchBar(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            inputField = {
                SearchBarDefaults.InputField(
                    onSearch = { conspiratorsViewModel.searchExpanded.value = false },
                    expanded = conspiratorsViewModel.searchExpanded.value,
                    onExpandedChange = { conspiratorsViewModel.searchExpanded.value = it },
                    placeholder = { Text("Search for a board or user") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    trailingIcon = { Icon(Icons.Default.MoreVert, contentDescription = null) },
                    query = "Nothing",
                    onQueryChange = {Toast.makeText(localContext, "Would display search results for boards and users", Toast.LENGTH_SHORT).show()},
                    modifier = Modifier.padding(2.dp),
                    enabled = true,
                    colors = TextFieldDefaults.colors(),
                )
            },
            expanded = conspiratorsViewModel.searchExpanded.value,
            onExpandedChange = { conspiratorsViewModel.searchExpanded.value = it },
            shape = SearchBarDefaults.fullScreenShape,
            colors = SearchBarDefaults.colors(),
            tonalElevation = 3.dp,
            shadowElevation = 2.dp,
            windowInsets = SearchBarDefaults.windowInsets
        ) {
            Text(text = "YOU SEARCHED!!! YAYYYYY")
        }
        val context = LocalContext.current
        LazyVerticalGrid (modifier = Modifier.fillMaxSize().padding(horizontal = 5.dp, vertical = 3.dp), columns = GridCells.Fixed(2))
        {
            items(conspiratorsViewModel.boards) { item ->
                BoardCard(title = item.name, image = R.drawable.sample_image, onClick = { Toast.makeText(context, "Would take you to view this board", Toast.LENGTH_SHORT).show()}, userName = conspiratorsViewModel.getUserNameByUUID(item.userUUID))
            }
        }
    }
    
}