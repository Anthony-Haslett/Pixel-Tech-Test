package com.example.pixeltechtest.ui.navigation

import android.content.Context
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.pixeltechtest.data.repository.UserRepository
import com.example.pixeltechtest.ui.theme.PixelTechTestTheme
import com.example.pixeltechtest.ui.viewmodel.UsersViewModel
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

private val Context.testDataStore: DataStore<Preferences> by preferencesDataStore("test_preferences")

@RunWith(AndroidJUnit4::class)
class MainNavigationTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val context = InstrumentationRegistry.getInstrumentation().targetContext
    private val dataStore = context.testDataStore
    private val userRepository = UserRepository(dataStore)
    private val viewModel = UsersViewModel(userRepository)

    @Test
    fun allUsersScreen_showsSearchIcon() {
        composeTestRule.setContent {
            PixelTechTestTheme {
                MainNavigationScreen(viewModel = viewModel)
            }
        }

        // Should be on All Users screen by default
        composeTestRule
            .onNodeWithContentDescription("Search")
            .assertIsDisplayed()

        // Should show the main title
        composeTestRule
            .onNodeWithText("StackOverflow Users")
            .assertIsDisplayed()

        // Should NOT show back button on main screen
        composeTestRule
            .onNodeWithContentDescription("Back")
            .assertDoesNotExist()
    }

    @Test
    fun followingScreen_hidesSearchIcon_showsBackButton() {
        composeTestRule.setContent {
            PixelTechTestTheme {
                MainNavigationScreen(viewModel = viewModel)
            }
        }

        // Navigate to Following screen
        composeTestRule
            .onNodeWithText("Following (0)")
            .performClick()

        // Wait for navigation to complete
        composeTestRule.waitForIdle()

        // Should show "Following" title
        composeTestRule
            .onNodeWithText("Following")
            .assertIsDisplayed()

        // Should show back button
        composeTestRule
            .onNodeWithContentDescription("Back")
            .assertIsDisplayed()

        // Should NOT show search icon
        composeTestRule
            .onNodeWithContentDescription("Search")
            .assertDoesNotExist()

        // Should NOT show main title
        composeTestRule
            .onNodeWithText("StackOverflow Users")
            .assertDoesNotExist()
    }

    @Test
    fun followingScreen_backButtonNavigatesToAllUsers() {
        composeTestRule.setContent {
            PixelTechTestTheme {
                MainNavigationScreen(viewModel = viewModel)
            }
        }

        // Navigate to Following screen
        composeTestRule
            .onNodeWithText("Following (0)")
            .performClick()

        composeTestRule.waitForIdle()

        // Verify we're on Following screen
        composeTestRule
            .onNodeWithText("Following")
            .assertIsDisplayed()

        // Click back button
        composeTestRule
            .onNodeWithContentDescription("Back")
            .performClick()

        composeTestRule.waitForIdle()

        // Should be back to All Users screen
        composeTestRule
            .onNodeWithText("StackOverflow Users")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithContentDescription("Search")
            .assertIsDisplayed()
    }

    @Test
    fun searchFunctionality_onlyWorksOnAllUsersScreen() {
        composeTestRule.setContent {
            PixelTechTestTheme {
                MainNavigationScreen(viewModel = viewModel)
            }
        }

        // On All Users screen - search should work
        composeTestRule
            .onNodeWithContentDescription("Search")
            .performClick()

        composeTestRule.waitForIdle()

        // Should show search text field
        composeTestRule
            .onNodeWithText("Search users...")
            .assertIsDisplayed()

        // Should show close search button
        composeTestRule
            .onNodeWithContentDescription("Close Search")
            .assertIsDisplayed()

        // Close search
        composeTestRule
            .onNodeWithContentDescription("Close Search")
            .performClick()

        composeTestRule.waitForIdle()

        // Navigate to Following screen
        composeTestRule
            .onNodeWithText("Following (0)")
            .performClick()

        composeTestRule.waitForIdle()

        // On Following screen - search should not be available
        composeTestRule
            .onNodeWithContentDescription("Search")
            .assertDoesNotExist()

        // Should not show search text field
        composeTestRule
            .onNodeWithText("Search users...")
            .assertDoesNotExist()
    }

    @Test
    fun searchState_doesNotAffectFollowingScreen() {
        composeTestRule.setContent {
            PixelTechTestTheme {
                MainNavigationScreen(viewModel = viewModel)
            }
        }

        // Activate search on All Users screen
        composeTestRule
            .onNodeWithContentDescription("Search")
            .performClick()

        composeTestRule.waitForIdle()

        // Verify search is active
        composeTestRule
            .onNodeWithText("Search users...")
            .assertIsDisplayed()

        // Navigate to Following screen while search is active
        composeTestRule
            .onNodeWithText("Following (0)")
            .performClick()

        composeTestRule.waitForIdle()

        // Following screen should still show normal UI (not search UI)
        composeTestRule
            .onNodeWithText("Following")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithContentDescription("Back")
            .assertIsDisplayed()

        // Search UI should not be visible on Following screen
        composeTestRule
            .onNodeWithText("Search users...")
            .assertDoesNotExist()

        composeTestRule
            .onNodeWithContentDescription("Close Search")
            .assertDoesNotExist()
    }
}
