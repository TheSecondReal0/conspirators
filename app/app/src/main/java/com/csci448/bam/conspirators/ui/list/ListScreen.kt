package com.csci448.bam.conspirators.ui.list

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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.csci448.bam.conspirators.R
import com.csci448.bam.conspirators.ui.sharedComponents.BoardCard
import com.csci448.bam.conspirators.viewmodel.ConspiratorsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListScreen(modifier: Modifier, conspiratorsViewModel: ConspiratorsViewModel) {
    Column(modifier = Modifier.fillMaxSize()) {
        var search by remember { mutableStateOf("") }
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
                    query = search,
                    onQueryChange = {q: String -> search = q;},
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
        LazyVerticalGrid (modifier = Modifier.fillMaxSize().padding(horizontal = 5.dp, vertical = 3.dp), columns = GridCells.Fixed(2))
        {
//            items(conspiratorsViewModel.boards) { item ->
            items(conspiratorsViewModel.firebaseBoardValues) { item ->
//                BoardCard(title = item.name, image = R.drawable.sample_image, onClick = {}, userName = conspiratorsViewModel.getUserNameByUUID(item.userUUID))
                BoardCard(title = item.name, imageUrl = "", onClick = {}, userName = "")
            }
        }
    }
    
}