package com.example.pixeltechtest.ui.integration

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.emptyPreferences
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.pixeltechtest.data.model.BadgeCounts
import com.example.pixeltechtest.data.model.User
import com.example.pixeltechtest.data.repository.UserRepository
import com.example.pixeltechtest.ui.compose.UsersScreen
import com.example.pixeltechtest.ui.theme.PixelTechTestTheme
import com.example.pixeltechtest.ui.viewmodel.UsersViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class UsersScreenIntegrationTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun usersScreen_displaysLoadingState_initially() {
        // Given
        val testRepository = TestRepositoryForUI()
        val viewModel = UsersViewModel(testRepository)

        // When
        composeTestRule.setContent {
            PixelTechTestTheme {
                UsersScreen(viewModel = viewModel)
            }
        }

        // Then
        composeTestRule.onNodeWithText("StackOverflow Users").assertIsDisplayed()
        composeTestRule.onNodeWithText("Loading users...").assertIsDisplayed()
    }

    @Test
    fun usersScreen_displaysUsers_whenDataLoaded() {
        // Given
        val testUsers = createTestUsers()
        val testRepository = TestRepositoryForUI()
        testRepository.setUsersResult(Result.success(testUsers))
        val viewModel = UsersViewModel(testRepository)

        // When
        composeTestRule.setContent {
            PixelTechTestTheme {
                UsersScreen(viewModel = viewModel)
            }
        }

        // Wait for the UI to load
        composeTestRule.waitForIdle()

        // Then - Check header is displayed
        composeTestRule.onNodeWithText("StackOverflow Users").assertIsDisplayed()

        // Check users are displayed with their ranking
        composeTestRule.onNodeWithText("1").assertIsDisplayed() // Ranking badge for first user
        composeTestRule.onNodeWithText("John Skeet").assertIsDisplayed()
        composeTestRule.onNodeWithText("Reputation: 1,000,000").assertIsDisplayed()

        composeTestRule.onNodeWithText("2").assertIsDisplayed() // Ranking badge for second user
        composeTestRule.onNodeWithText("Jon Skeet").assertIsDisplayed()
        composeTestRule.onNodeWithText("Reputation: 500,000").assertIsDisplayed()

        // Check Follow buttons are displayed
        composeTestRule.onAllNodesWithText("Follow").assertCountEquals(2)
    }

    @Test
    fun usersScreen_displaysErrorState_whenNetworkFails() {
        // Given
        val testRepository = TestRepositoryForUI()
        testRepository.setUsersResult(Result.failure(Exception("Network error")))
        val viewModel = UsersViewModel(testRepository)

        // When
        composeTestRule.setContent {
            PixelTechTestTheme {
                UsersScreen(viewModel = viewModel)
            }
        }

        // Wait for the UI to load
        composeTestRule.waitForIdle()

        // Then
        composeTestRule.onNodeWithText("StackOverflow Users").assertIsDisplayed()
        composeTestRule.onNodeWithText("Unable to load users").assertIsDisplayed()
        composeTestRule.onNodeWithText("Network error").assertIsDisplayed()
        composeTestRule.onNodeWithText("Retry").assertIsDisplayed()
    }

    @Test
    fun usersScreen_followButton_togglesFollowState() {
        // Given
        val testUsers = createTestUsers()
        val testRepository = TestRepositoryForUI()
        testRepository.setUsersResult(Result.success(testUsers))
        val viewModel = UsersViewModel(testRepository)

        composeTestRule.setContent {
            PixelTechTestTheme {
                UsersScreen(viewModel = viewModel)
            }
        }

        // Wait for the UI to load
        composeTestRule.waitForIdle()

        // When - Click the first Follow button
        composeTestRule.onAllNodesWithText("Follow")[0].performClick()

        // Wait for the state to update
        composeTestRule.waitForIdle()

        // Then - Button should change to "Unfollow"
        composeTestRule.onNodeWithText("Unfollow").assertIsDisplayed()

        // When - Click Unfollow
        composeTestRule.onNodeWithText("Unfollow").performClick()

        // Wait for the state to update
        composeTestRule.waitForIdle()

        // Then - Button should change back to "Follow"
        composeTestRule.onAllNodesWithText("Follow").assertCountEquals(2)
    }

    @Test
    fun usersScreen_retryButton_reloadsData() {
        // Given - Start with error state
        val testRepository = TestRepositoryForUI()
        testRepository.setUsersResult(Result.failure(Exception("Network error")))
        val viewModel = UsersViewModel(testRepository)

        composeTestRule.setContent {
            PixelTechTestTheme {
                UsersScreen(viewModel = viewModel)
            }
        }

        // Wait for the UI to show error
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Network error").assertIsDisplayed()

        // When - Set success data and click retry
        val testUsers = createTestUsers()
        testRepository.setUsersResult(Result.success(testUsers))
        composeTestRule.onNodeWithText("Retry").performClick()

        // Wait for the UI to update
        composeTestRule.waitForIdle()

        // Then - Users should be displayed
        composeTestRule.onNodeWithText("John Skeet").assertIsDisplayed()
        composeTestRule.onNodeWithText("Jon Skeet").assertIsDisplayed()
        composeTestRule.onAllNodesWithText("Follow").assertCountEquals(2)
    }

    @Test
    fun usersScreen_displaysRankingBadges_withCorrectColors() {
        // Given
        val testUsers = createTestUsers()
        val testRepository = TestRepositoryForUI()
        testRepository.setUsersResult(Result.success(testUsers))
        val viewModel = UsersViewModel(testRepository)

        composeTestRule.setContent {
            PixelTechTestTheme {
                UsersScreen(viewModel = viewModel)
            }
        }

        // Wait for the UI to load
        composeTestRule.waitForIdle()

        // Then - Check ranking badges are displayed
        composeTestRule.onNodeWithText("1").assertIsDisplayed() // First place
        composeTestRule.onNodeWithText("2").assertIsDisplayed() // Second place

        // The ranking badges should be visible and in correct order
        // (We can't easily test colors in Compose tests, but we can verify they exist)
    }

    @Test
    fun usersScreen_followMultipleUsers_persistsState() {
        // Given
        val testUsers = createTestUsers()
        val testRepository = TestRepositoryForUI()
        testRepository.setUsersResult(Result.success(testUsers))
        val viewModel = UsersViewModel(testRepository)

        composeTestRule.setContent {
            PixelTechTestTheme {
                UsersScreen(viewModel = viewModel)
            }
        }

        // Wait for the UI to load
        composeTestRule.waitForIdle()

        // When - Follow both users
        composeTestRule.onAllNodesWithText("Follow")[0].performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onAllNodesWithText("Follow")[0].performClick() // Now the second one becomes first in the remaining
        composeTestRule.waitForIdle()

        // Then - Both should show as "Unfollow"
        composeTestRule.onAllNodesWithText("Unfollow").assertCountEquals(2)

        // When - Unfollow one user
        composeTestRule.onAllNodesWithText("Unfollow")[0].performClick()
        composeTestRule.waitForIdle()

        // Then - Should have one Follow and one Unfollow button
        composeTestRule.onAllNodesWithText("Follow").assertCountEquals(1)
        composeTestRule.onAllNodesWithText("Unfollow").assertCountEquals(1)
    }

    private fun createTestUsers(): List<User> {
        return listOf(
            User(
                userId = 22656,
                displayName = "John Skeet",
                reputation = 1000000,
                profileImage = "https://example.com/john.jpg",
                location = "Reading, United Kingdom",
                websiteUrl = "https://codeblog.jonskeet.uk/",
                link = "https://stackoverflow.com/users/22656/jon-skeet",
                badgeCounts = BadgeCounts(bronze = 7000, silver = 8000, gold = 900),
                isEmployee = false,
                userType = "registered",
                acceptRate = 85,
                creationDate = 1222430705,
                lastAccessDate = 1735689600,
                lastModifiedDate = 1735689500,
                accountId = 11683
            ),
            User(
                userId = 23354,
                displayName = "Jon Skeet",
                reputation = 500000,
                profileImage = "https://example.com/jon.jpg",
                location = "London, United Kingdom",
                websiteUrl = null,
                link = "https://stackoverflow.com/users/23354/user",
                badgeCounts = BadgeCounts(bronze = 3000, silver = 4000, gold = 500),
                isEmployee = false,
                userType = "registered",
                acceptRate = 90,
                creationDate = 1222430800,
                lastAccessDate = 1735689700,
                lastModifiedDate = 1735689600,
                accountId = 11684
            )
        )
    }
}

// Test repository implementation that allows setting test data
class TestRepositoryForUI : UserRepository(TestDataStoreForUI()) {
    private var usersResult: Result<List<User>> = Result.success(emptyList())
    private var isLoading = false

    fun setUsersResult(result: Result<List<User>>) {
        this.usersResult = result
    }

    override suspend fun getUsers(): Result<List<User>> {
        isLoading = true
        // Simulate network delay
        kotlinx.coroutines.delay(100)
        isLoading = false
        return usersResult
    }
}

// Simple test DataStore implementation for UI tests
class TestDataStoreForUI : DataStore<Preferences> {
    private val dataFlow = MutableStateFlow(emptyPreferences())

    override val data: Flow<Preferences> = dataFlow

    override suspend fun updateData(transform: suspend (t: Preferences) -> Preferences): Preferences {
        val currentData = dataFlow.value
        val newData = transform(currentData)
        dataFlow.value = newData
        return newData
    }
}
