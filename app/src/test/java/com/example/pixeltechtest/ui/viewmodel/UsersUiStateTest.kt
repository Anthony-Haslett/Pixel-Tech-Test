package com.example.pixeltechtest.ui.viewmodel

import com.example.pixeltechtest.data.model.BadgeCounts
import com.example.pixeltechtest.data.model.User
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Test
import org.junit.Assert.*

@OptIn(ExperimentalCoroutinesApi::class)
class UsersUiStateTest {

    @Test
    fun `UsersUiState default values should be correct`() {
        // When
        val uiState = UsersUiState()

        // Then
        assertTrue(uiState.users.isEmpty())
        assertTrue(uiState.followedUsers.isEmpty())
        assertTrue(uiState.filteredUsers.isEmpty())
        assertTrue(uiState.isLoading) // Default is true, not false
        assertNull(uiState.error) // Property is 'error', not 'errorMessage'
        assertTrue(uiState.followingInProgress.isEmpty())
        assertEquals("", uiState.searchQuery)
        assertFalse(uiState.isSearchActive)
    }

    @Test
    fun `UsersUiState with custom values should work correctly`() {
        // Given
        val testUsers = listOf(
            User(
                userId = 1,
                displayName = "User 1",
                reputation = 10000,
                profileImage = "http://example.com/image.jpg", // Required parameter
                location = "Test City", // Required parameter (nullable)
                websiteUrl = "http://example.com", // Required parameter (nullable)
                link = "link1",
                badgeCounts = BadgeCounts(1, 2, 3),
                isEmployee = false,
                userType = "registered",
                acceptRate = 85, // Required parameter (nullable)
                creationDate = 123456,
                lastAccessDate = 123456,
                lastModifiedDate = 123456,
                accountId = 1
            )
        )
        val followedUsers = setOf(1, 2, 3)
        val followingInProgress = setOf(2)

        // When
        val uiState = UsersUiState(
            users = testUsers,
            filteredUsers = testUsers,
            followedUsers = followedUsers,
            followingInProgress = followingInProgress,
            isLoading = false,
            error = "Test error",
            searchQuery = "test",
            isSearchActive = true
        )

        // Then
        assertEquals(testUsers, uiState.users)
        assertEquals(testUsers, uiState.filteredUsers)
        assertEquals(followedUsers, uiState.followedUsers)
        assertEquals(followingInProgress, uiState.followingInProgress)
        assertFalse(uiState.isLoading)
        assertEquals("Test error", uiState.error)
        assertEquals("test", uiState.searchQuery)
        assertTrue(uiState.isSearchActive)
    }

    @Test
    fun `UsersUiState copy should work correctly`() {
        // Given
        val originalState = UsersUiState(
            users = emptyList(),
            followedUsers = emptySet(),
            isLoading = true,
            error = null
        )

        // When
        val newState = originalState.copy(
            isLoading = false,
            error = "New error"
        )

        // Then
        assertEquals(originalState.users, newState.users)
        assertEquals(originalState.followedUsers, newState.followedUsers)
        assertFalse(newState.isLoading)
        assertEquals("New error", newState.error)
    }

    @Test
    fun `UsersUiState equality should work correctly`() {
        // Given
        val state1 = UsersUiState(
            users = emptyList(),
            followedUsers = setOf(1, 2),
            isLoading = false,
            error = "Error"
        )
        val state2 = UsersUiState(
            users = emptyList(),
            followedUsers = setOf(1, 2),
            isLoading = false,
            error = "Error"
        )
        val state3 = UsersUiState(
            users = emptyList(),
            followedUsers = setOf(1, 2),
            isLoading = true, // Different value
            error = "Error"
        )

        // Then
        assertEquals(state1, state2)
        assertEquals(state1.hashCode(), state2.hashCode())
        assertNotEquals(state1, state3)
    }

    @Test
    fun `UsersUiState with nullable fields should work correctly`() {
        // Given
        val testUser = User(
            userId = 123,
            displayName = "Test User",
            reputation = 5000,
            profileImage = "http://example.com/image.jpg",
            location = null, // Nullable field
            websiteUrl = null, // Nullable field
            link = "http://stackoverflow.com/users/123",
            badgeCounts = BadgeCounts(bronze = 5, silver = 3, gold = 1),
            isEmployee = false,
            userType = "registered",
            acceptRate = null, // Nullable field
            creationDate = 1234567890,
            lastAccessDate = 1234567900,
            lastModifiedDate = 1234567895,
            accountId = 456
        )

        // When
        val uiState = UsersUiState(
            users = listOf(testUser),
            error = null
        )

        // Then
        assertEquals(1, uiState.users.size)
        assertEquals(testUser, uiState.users[0])
        assertNull(uiState.error)
    }

    @Test
    fun `UsersUiState search functionality should work correctly`() {
        // Given
        val uiState = UsersUiState()

        // When
        val searchState = uiState.copy(
            searchQuery = "kotlin",
            isSearchActive = true
        )

        // Then
        assertEquals("kotlin", searchState.searchQuery)
        assertTrue(searchState.isSearchActive)
        assertTrue(searchState.isLoading) // Should preserve other default values
    }
}
