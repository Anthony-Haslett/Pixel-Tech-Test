package com.example.pixeltechtest.ui.compose

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.pixeltechtest.ui.viewmodel.UsersViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FollowingScreen(
    viewModel: UsersViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // Filter to show only followed users
    val followedUsers = uiState.users.filter { user ->
        uiState.followedUsers.contains(user.userId)
    }

    when {
        uiState.isLoading -> {
            // Loading state
            Box(
                modifier = modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator()
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Loading users...",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }

        uiState.error != null -> {
            // Error state
            Box(
                modifier = modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Unable to load users",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = uiState.error ?: "Unknown error",
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.error
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = { viewModel.retry() }
                    ) {
                        Text("Retry")
                    }
                }
            }
        }

        followedUsers.isEmpty() -> {
            // Empty state - no followed users
            Box(
                modifier = modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "No Following Users Yet",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Start following users from the All Users tab to see them here!",
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        else -> {
            // Success state - display followed users
            LazyColumn(
                modifier = modifier.fillMaxSize(),
                contentPadding = PaddingValues(vertical = 8.dp)
            ) {
                itemsIndexed(followedUsers) { index, user ->
                    UserListItem(
                        user = user,
                        ranking = index + 1,
                        isFollowed = true, // All users in this list are followed
                        onFollowClick = { viewModel.toggleFollowUser(user.userId) }
                    )
                }
            }
        }
    }
}
