package com.example.pixeltechtest.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.pixeltechtest.ui.compose.FollowingScreen
import com.example.pixeltechtest.ui.compose.UsersScreen
import com.example.pixeltechtest.ui.viewmodel.UsersViewModel

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    object AllUsers : Screen("all_users", "All Users", Icons.Default.Person)
    object Following : Screen("following", "Following", Icons.Default.Favorite)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainNavigationScreen(
    viewModel: UsersViewModel,
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // Refresh followed users when the screen is first composed or recomposed
    LaunchedEffect(Unit) {
        viewModel.refreshFollowedUsers()
    }

    val items = listOf(
        Screen.AllUsers,
        Screen.Following
    )

    Scaffold(
        modifier = modifier,
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination

                items.forEach { screen ->
                    NavigationBarItem(
                        icon = {
                            Icon(
                                screen.icon,
                                contentDescription = screen.title
                            )
                        },
                        label = {
                            val displayText = if (screen == Screen.Following) {
                                "${screen.title} (${uiState.followedUsers.size})"
                            } else {
                                screen.title
                            }
                            Text(displayText)
                        },
                        selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                        onClick = {
                            navController.navigate(screen.route) {
                                // Pop up to the start destination of the graph to
                                // avoid building up a large stack of destinations
                                // on the back stack as users select items
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                // Avoid multiple copies of the same destination when
                                // reselecting the same item
                                launchSingleTop = true
                                // Restore state when reselecting a previously selected item
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.AllUsers.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.AllUsers.route) {
                UsersScreen(viewModel = viewModel)
            }
            composable(Screen.Following.route) {
                FollowingScreen(viewModel = viewModel)
            }
        }
    }
}
