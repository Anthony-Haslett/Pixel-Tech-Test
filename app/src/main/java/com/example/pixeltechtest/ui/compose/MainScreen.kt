package com.example.pixeltechtest.ui.compose

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.pixeltechtest.ui.viewmodel.UsersViewModel

enum class MainTab(val title: String, val icon: ImageVector) {
    ALL_USERS("All Users", Icons.Default.Person),
    FOLLOWING("Following", Icons.Default.Favorite)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    viewModel: UsersViewModel,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableStateOf(MainTab.ALL_USERS) }
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Column(modifier = modifier.fillMaxSize()) {
        // Tab Row
        TabRow(
            selectedTabIndex = selectedTab.ordinal
        ) {
            MainTab.entries.forEach { tab ->
                Tab(
                    selected = selectedTab == tab,
                    onClick = { selectedTab = tab },
                    icon = {
                        Icon(
                            imageVector = tab.icon,
                            contentDescription = tab.title
                        )
                    },
                    text = {
                        val displayText = if (tab == MainTab.FOLLOWING) {
                            val followedCount = uiState.followedUsers.size
                            "${tab.title} ($followedCount)"
                        } else {
                            tab.title
                        }
                        Text(displayText)
                    }
                )
            }
        }

        // Content based on selected tab
        when (selectedTab) {
            MainTab.ALL_USERS -> {
                UsersScreen(
                    viewModel = viewModel,
                    modifier = Modifier.fillMaxSize()
                )
            }
            MainTab.FOLLOWING -> {
                FollowingScreen(
                    viewModel = viewModel,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}
