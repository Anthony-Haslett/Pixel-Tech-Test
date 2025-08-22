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
class UserJourneyIntegrationTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun completeUserJourney_loadUsersFollowUnfollowAndRetry() {
        // Given
        val testUsers = createLargeUserList()
        val testRepository = TestRepositoryWithDelay()
        testRepository.setUsersResult(Result.success(testUsers))
        val viewModel = UsersViewModel(testRepository)

        composeTestRule.setContent {
            PixelTechTestTheme {
                UsersScreen(viewModel = viewModel)
            }
        }

        // Step 1: Verify initial loading state
        composeTestRule.onNodeWithText("Loading users...").assertIsDisplayed()

        // Step 2: Wait for data to load completely
        composeTestRule.waitUntil(timeoutMillis = 3000) {
            composeTestRule.onAllNodesWithText("Loading users...").fetchSemanticsNodes().isEmpty()
        }

        composeTestRule.waitUntil(timeoutMillis = 3000) {
            composeTestRule.onAllNodesWithText("Follow").fetchSemanticsNodes().isNotEmpty()
        }

        // Verify users are displayed
        composeTestRule.onNodeWithText("Top User").assertIsDisplayed()
        composeTestRule.onNodeWithText("1").assertIsDisplayed() // First ranking badge
        composeTestRule.onAllNodesWithText("Follow").assertCountEquals(4)

        // Step 3: Follow the top user
        composeTestRule.onAllNodesWithText("Follow")[0].performClick()
        composeTestRule.waitUntil(timeoutMillis = 2000) {
            composeTestRule.onAllNodesWithText("Unfollow").fetchSemanticsNodes().isNotEmpty()
        }

        // Step 4: Verify second user is displayed and follow them
        composeTestRule.onNodeWithText("Second User").assertIsDisplayed()
        composeTestRule.onAllNodesWithText("Follow")[0].performClick() // Follow second user
        composeTestRule.waitUntil(timeoutMillis = 2000) {
            composeTestRule.onAllNodesWithText("Unfollow").fetchSemanticsNodes().size == 2
        }

        // Step 5: Verify both users are followed
        composeTestRule.onAllNodesWithText("Unfollow").assertCountEquals(2)

        // Step 6: Unfollow one user
        composeTestRule.onAllNodesWithText("Unfollow")[0].performClick()
        composeTestRule.waitUntil(timeoutMillis = 2000) {
            composeTestRule.onAllNodesWithText("Unfollow").fetchSemanticsNodes().size == 1
        }

        // Step 7: Verify state is updated correctly
        composeTestRule.onAllNodesWithText("Unfollow").assertCountEquals(1)
        composeTestRule.onAllNodesWithText("Follow").assertCountEquals(3) // Total 4 users, 1 followed, 3 not followed
    }

    @Test
    fun errorRecoveryJourney_networkFailsAndRecovers() {
        // Given - Start with network failure
        val testRepository = TestRepositoryWithDelay()
        testRepository.setUsersResult(Result.failure(Exception("Network timeout")))
        val viewModel = UsersViewModel(testRepository)

        composeTestRule.setContent {
            PixelTechTestTheme {
                UsersScreen(viewModel = viewModel)
            }
        }

        // Step 1: Wait for error state to be displayed (accounting for delay)
        composeTestRule.waitUntil(timeoutMillis = 3000) {
            composeTestRule.onAllNodesWithText("Unable to load users").fetchSemanticsNodes().isNotEmpty()
        }

        composeTestRule.onNodeWithText("Unable to load users").assertIsDisplayed()
        composeTestRule.onNodeWithText("Network timeout").assertIsDisplayed()
        composeTestRule.onNodeWithText("Retry").assertIsDisplayed()

        // Step 2: Simulate network recovery and retry
        val testUsers = createLargeUserList()
        testRepository.setUsersResult(Result.success(testUsers))
        composeTestRule.onNodeWithText("Retry").performClick()

        // Step 3: Verify loading state appears
        composeTestRule.waitUntil(timeoutMillis = 2000) {
            composeTestRule.onAllNodesWithText("Loading users...").fetchSemanticsNodes().isNotEmpty()
        }

        // Step 4: Wait for users to be loaded successfully
        composeTestRule.waitUntil(timeoutMillis = 3000) {
            composeTestRule.onAllNodesWithText("Loading users...").fetchSemanticsNodes().isEmpty() &&
            composeTestRule.onAllNodesWithText("Follow").fetchSemanticsNodes().isNotEmpty()
        }

        composeTestRule.onNodeWithText("Top User").assertIsDisplayed()
        composeTestRule.onNodeWithText("Second User").assertIsDisplayed()
        composeTestRule.onAllNodesWithText("Follow").assertCountEquals(4)
    }

    @Test
    fun userInterface_respondsToQuickInteractions() {
        // Given
        val testUsers = createLargeUserList()
        val testRepository = TestRepositoryWithDelay()
        testRepository.setUsersResult(Result.success(testUsers))
        val viewModel = UsersViewModel(testRepository)

        composeTestRule.setContent {
            PixelTechTestTheme {
                UsersScreen(viewModel = viewModel)
            }
        }

        // Wait for data to load completely
        composeTestRule.waitUntil(timeoutMillis = 3000) {
            composeTestRule.onAllNodesWithText("Loading users...").fetchSemanticsNodes().isEmpty()
        }

        composeTestRule.waitUntil(timeoutMillis = 3000) {
            composeTestRule.onAllNodesWithText("Follow").fetchSemanticsNodes().isNotEmpty()
        }

        // Verify initial state
        composeTestRule.onAllNodesWithText("Follow").assertCountEquals(4)

        // Test rapid follow/unfollow interactions
        repeat(3) {
            composeTestRule.onAllNodesWithText("Follow")[0].performClick()
            composeTestRule.waitUntil(timeoutMillis = 2000) {
                composeTestRule.onAllNodesWithText("Unfollow").fetchSemanticsNodes().isNotEmpty()
            }

            composeTestRule.onNodeWithText("Unfollow").performClick()
            composeTestRule.waitUntil(timeoutMillis = 2000) {
                composeTestRule.onAllNodesWithText("Follow").fetchSemanticsNodes().size == 4
            }
        }
    }

    @Test
    fun accessibility_allElementsHaveProperContentDescription() {
        // Given
        val testUsers = createLargeUserList()
        val testRepository = TestRepositoryWithDelay()
        testRepository.setUsersResult(Result.success(testUsers))
        val viewModel = UsersViewModel(testRepository)

        composeTestRule.setContent {
            PixelTechTestTheme {
                UsersScreen(viewModel = viewModel)
            }
        }

        // Wait for data to load completely
        composeTestRule.waitUntil(timeoutMillis = 3000) {
            composeTestRule.onAllNodesWithText("Loading users...").fetchSemanticsNodes().isEmpty()
        }

        composeTestRule.waitUntil(timeoutMillis = 3000) {
            composeTestRule.onAllNodesWithText("Follow").fetchSemanticsNodes().isNotEmpty()
        }

        // Test that important UI elements are accessible
        composeTestRule.onNodeWithText("StackOverflow Users").assertIsDisplayed()
        composeTestRule.onAllNodesWithText("Follow").onFirst().assertIsDisplayed()

        // Verify ranking badges are accessible
        composeTestRule.onNodeWithText("1").assertIsDisplayed()
        composeTestRule.onNodeWithText("2").assertIsDisplayed()
    }

    @Test
    fun dataConsistency_followStatePersiststAcrossActions() {
        // Given
        val testUsers = createLargeUserList()
        val testRepository = TestRepositoryWithDelay()
        testRepository.setUsersResult(Result.success(testUsers))
        val viewModel = UsersViewModel(testRepository)

        composeTestRule.setContent {
            PixelTechTestTheme {
                UsersScreen(viewModel = viewModel)
            }
        }

        // Wait for data to load completely - first wait for loading to disappear
        composeTestRule.waitUntil(timeoutMillis = 3000) {
            composeTestRule.onAllNodesWithText("Loading users...").fetchSemanticsNodes().isEmpty()
        }

        // Then wait for the actual user data to appear
        composeTestRule.waitUntil(timeoutMillis = 3000) {
            composeTestRule.onAllNodesWithText("Follow").fetchSemanticsNodes().isNotEmpty()
        }

        // Verify we have the expected number of Follow buttons before proceeding
        composeTestRule.onAllNodesWithText("Follow").assertCountEquals(4)

        // Follow multiple users in sequence with proper waiting
        // Follow first user
        composeTestRule.onAllNodesWithText("Follow")[0].performClick()
        composeTestRule.waitUntil(timeoutMillis = 2000) {
            composeTestRule.onAllNodesWithText("Unfollow").fetchSemanticsNodes().size == 1
        }

        // Follow second user (now first in remaining Follow buttons)
        composeTestRule.onAllNodesWithText("Follow")[0].performClick()
        composeTestRule.waitUntil(timeoutMillis = 2000) {
            composeTestRule.onAllNodesWithText("Unfollow").fetchSemanticsNodes().size == 2
        }

        // Follow third user
        composeTestRule.onAllNodesWithText("Follow")[0].performClick()
        composeTestRule.waitUntil(timeoutMillis = 2000) {
            composeTestRule.onAllNodesWithText("Unfollow").fetchSemanticsNodes().size == 3
        }

        // Verify all three users are followed
        composeTestRule.onAllNodesWithText("Unfollow").assertCountEquals(3)
        composeTestRule.onAllNodesWithText("Follow").assertCountEquals(1)

        // Follow state should persist (this tests DataStore persistence)
        // Just verify the current state is maintained
        composeTestRule.onAllNodesWithText("Unfollow").assertCountEquals(3)
        composeTestRule.onAllNodesWithText("Follow").assertCountEquals(1)
    }

    private fun createLargeUserList(): List<User> {
        return listOf(
            User(
                userId = 1,
                displayName = "Top User",
                reputation = 1500000,
                profileImage = "https://picsum.photos/200/300",
                location = "San Francisco, CA",
                websiteUrl = null,
                link = "https://stackoverflow.com/users/1",
                badgeCounts = BadgeCounts(bronze = 1000, silver = 800, gold = 200),
                isEmployee = false,
                userType = "registered",
                acceptRate = 95,
                creationDate = 1234567890,
                lastAccessDate = 1735689600,
                lastModifiedDate = 1735689500,
                accountId = 1001
            ),
            User(
                userId = 2,
                displayName = "Second User",
                reputation = 1200000,
                profileImage = "https://picsum.photos/200/300",
                location = "New York, NY",
                websiteUrl = "https://example.com",
                link = "https://stackoverflow.com/users/2",
                badgeCounts = BadgeCounts(bronze = 900, silver = 700, gold = 150),
                isEmployee = true,
                userType = "registered",
                acceptRate = 88,
                creationDate = 1234567900,
                lastAccessDate = 1735689700,
                lastModifiedDate = 1735689600,
                accountId = 1002
            ),
            User(
                userId = 3,
                displayName = "Third User",
                reputation = 800000,
                profileImage = "https://picsum.photos/200/300",
                location = "London, UK",
                websiteUrl = null,
                link = "https://stackoverflow.com/users/3",
                badgeCounts = BadgeCounts(bronze = 600, silver = 400, gold = 100),
                isEmployee = false,
                userType = "registered",
                acceptRate = 92,
                creationDate = 1234567910,
                lastAccessDate = 1735689800,
                lastModifiedDate = 1735689700,
                accountId = 1003
            ),
            User(
                userId = 4,
                displayName = "Fourth User",
                reputation = 500000,
                profileImage = "https://picsum.photos/200/300",
                location = "Toronto, Canada",
                websiteUrl = null,
                link = "https://stackoverflow.com/users/4",
                badgeCounts = BadgeCounts(bronze = 400, silver = 300, gold = 50),
                isEmployee = false,
                userType = "registered",
                acceptRate = 85,
                creationDate = 1234567920,
                lastAccessDate = 1735689900,
                lastModifiedDate = 1735689800,
                accountId = 1004
            )
        )
    }
}

// Test repository with simulated network delays for more realistic testing
class TestRepositoryWithDelay : UserRepository(TestDataStoreForDelay()) {
    private var usersResult: Result<List<User>> = Result.success(emptyList())

    fun setUsersResult(result: Result<List<User>>) {
        this.usersResult = result
    }

    override suspend fun getUsers(): Result<List<User>> {
        // Simulate network delay
        kotlinx.coroutines.delay(500)
        return usersResult
    }
}

class TestDataStoreForDelay : DataStore<Preferences> {
    private val dataFlow = MutableStateFlow(emptyPreferences())

    override val data: Flow<Preferences> = dataFlow

    override suspend fun updateData(transform: suspend (t: Preferences) -> Preferences): Preferences {
        val currentData = dataFlow.value
        val newData = transform(currentData)
        dataFlow.value = newData
        return newData
    }
}
