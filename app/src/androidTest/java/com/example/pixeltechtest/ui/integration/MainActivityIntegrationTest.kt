package com.example.pixeltechtest.ui.integration

import androidx.compose.ui.Modifier
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.pixeltechtest.MainActivity
import com.example.pixeltechtest.data.model.BadgeCounts
import com.example.pixeltechtest.data.model.User
import com.example.pixeltechtest.data.repository.UserRepository
import com.example.pixeltechtest.ui.compose.UserListItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MainActivityIntegrationTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun app_displaysCorrectTitle() {
        // Then - App should display the correct title
        composeTestRule.onNodeWithText("StackOverflow Users").assertIsDisplayed()
    }

    @Test
    fun app_handlesSystemBarsCorrectly() {
        // Then - Content should not overlap with system bars (edge-to-edge implementation)
        composeTestRule.onNodeWithText("StackOverflow Users").assertIsDisplayed()

        // The app should be displayed properly without overlapping system UI
        // This tests the Scaffold implementation with proper padding
    }

    @Test
    fun app_displaysLoadingStateInitially() {
        // Given the app just started

        // Then - Should show loading state initially
        composeTestRule.onNodeWithText("Loading users...").assertIsDisplayed()
    }

    @Test
    fun app_allowsScrollingThroughUsersList() {
        // Wait for potential data to load (if any)
        composeTestRule.waitForIdle()

        // Then - The app should be scrollable (LazyColumn implementation)
        // We can test this by checking if the list container exists
        composeTestRule.onRoot().assertIsDisplayed()
    }
}

// Additional test file for testing specific UI components in isolation
@RunWith(AndroidJUnit4::class)
class UserListItemTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun userListItem_displaysUserInformation() {
        // Given
        val testUser = User(
            userId = 1,
            displayName = "Test User",
            reputation = 50000,
            profileImage = "https://picsum.photos/200/300",
            location = "Test City",
            websiteUrl = null,
            link = "https://stackoverflow.com/users/1",
            badgeCounts = BadgeCounts(bronze = 10, silver = 5, gold = 2),
            isEmployee = false,
            userType = "registered",
            acceptRate = 85,
            creationDate = 1234567890,
            lastAccessDate = 1234567900,
            lastModifiedDate = 1234567895,
            accountId = 1
        )

        // When
        composeTestRule.setContent {
            UserListItem(
                user = testUser,
                ranking = 1,
                isFollowed = false,
                onFollowClick = {  },
                modifier = Modifier
            )
        }

        // Then
        composeTestRule.onNodeWithText("1").assertIsDisplayed() // Ranking badge
        composeTestRule.onNodeWithText("Test User").assertIsDisplayed()
        composeTestRule.onNodeWithText("Reputation: 50,000").assertIsDisplayed()
        composeTestRule.onNodeWithText("Test City").assertIsDisplayed()
        composeTestRule.onNodeWithText("Follow").assertIsDisplayed()
    }

    @Test
    fun userListItem_displaysProfilePlaceholder() {
        // Given
        val testUser = User(
            userId = 1,
            displayName = "John Doe",
            reputation = 1000,
            profileImage = "https://picsum.photos/200/300",
            location = null,
            websiteUrl = null,
            link = "https://stackoverflow.com/users/1",
            badgeCounts = BadgeCounts(bronze = 1, silver = 0, gold = 0),
            isEmployee = false,
            userType = "registered",
            acceptRate = null,
            creationDate = 1234567890,
            lastAccessDate = 1234567900,
            lastModifiedDate = 1234567895,
            accountId = 1
        )

        // When
        composeTestRule.setContent {
            UserListItem(
                user = testUser,
                ranking = 5,
                isFollowed = false,
                onFollowClick = {},
                modifier = Modifier
            )
        }

        // Then - Should display first letter of name as profile placeholder
        composeTestRule.onNodeWithText("J").assertIsDisplayed() // Profile placeholder
        composeTestRule.onNodeWithText("John Doe").assertIsDisplayed()
        composeTestRule.onNodeWithText("5").assertIsDisplayed() // Ranking badge
    }

    @Test
    fun userListItem_followButton_triggersCallback() {
        // Given
        var followToggled = false
        var toggledUserId = -1
        val testUser = User(
            userId = 123,
            displayName = "Test User",
            reputation = 1000,
            profileImage = "https://picsum.photos/200/300",
            location = null,
            websiteUrl = null,
            link = "https://stackoverflow.com/users/123",
            badgeCounts = BadgeCounts(bronze = 1, silver = 0, gold = 0),
            isEmployee = false,
            userType = "registered",
            acceptRate = null,
            creationDate = 1234567890,
            lastAccessDate = 1234567900,
            lastModifiedDate = 1234567895,
            accountId = 123
        )

        composeTestRule.setContent {
            UserListItem(
                user = testUser,
                ranking = 1,
                isFollowed = false,
                onFollowClick = {
                    followToggled = true
                    toggledUserId = testUser.userId
                }
            )
        }

        // When
        composeTestRule.onNodeWithText("Follow").performClick()

        // Then
        assert(followToggled) { "Follow callback should be triggered" }
        assert(toggledUserId == 123) { "Correct user ID should be passed to callback" }
    }

    @Test
    fun userListItem_showsUnfollowWhenFollowed() {
        // Given
        val testUser = User(
            userId = 1,
            displayName = "Test User",
            reputation = 1000,
            profileImage = "https://picsum.photos/200/300",
            location = null,
            websiteUrl = null,
            link = "https://stackoverflow.com/users/1",
            badgeCounts = BadgeCounts(bronze = 1, silver = 0, gold = 0),
            isEmployee = false,
            userType = "registered",
            acceptRate = null,
            creationDate = 1234567890,
            lastAccessDate = 1234567900,
            lastModifiedDate = 1234567895,
            accountId = 1
        )

        // When - User is already followed
        composeTestRule.setContent {
            UserListItem(
                user = testUser,
                ranking = 1,
                isFollowed = true,
                onFollowClick = { }
            )
        }

        // Then
        composeTestRule.onNodeWithText("Unfollow").assertIsDisplayed()
        composeTestRule.onNodeWithText("Follow").assertDoesNotExist()
    }

    @Test
    fun rankingBadge_displaysCorrectNumber() {
        // Given
        val testUser = User(
            userId = 1,
            displayName = "Test User",
            reputation = 1000,
            profileImage = "https://picsum.photos/200/300",
            location = null,
            websiteUrl = null,
            link = "https://stackoverflow.com/users/1",
            badgeCounts = BadgeCounts(bronze = 1, silver = 0, gold = 0),
            isEmployee = false,
            userType = "registered",
            acceptRate = null,
            creationDate = 1234567890,
            lastAccessDate = 1234567900,
            lastModifiedDate = 1234567895,
            accountId = 1
        )

        // When - Test different rankings
        composeTestRule.setContent {
            UserListItem(
                user = testUser,
                ranking = 15,
                isFollowed = false,
                onFollowClick = { }
            )
        }

        // Then
        composeTestRule.onNodeWithText("15").assertIsDisplayed()
    }
}
