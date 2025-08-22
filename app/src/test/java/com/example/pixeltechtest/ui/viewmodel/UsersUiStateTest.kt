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
        assertFalse(uiState.isLoading)
        assertNull(uiState.errorMessage)
    }

    @Test
    fun `UsersUiState with custom values should work correctly`() {
        // Given
        val testUsers = listOf(
            User(
                userId = 1,
                displayName = "User 1",
                reputation = 10000,
                link = "link1",
                badgeCounts = BadgeCounts(1, 2, 3),
                isEmployee = false,
                userType = "registered",
                creationDate = 123456,
                lastAccessDate = 123456,
                lastModifiedDate = 123456,
                accountId = 1
            )
        )
        val followedUsers = setOf(1, 2, 3)

        // When
        val uiState = UsersUiState(
            users = testUsers,
            followedUsers = followedUsers,
            isLoading = true,
            errorMessage = "Test error"
        )

        // Then
        assertEquals(testUsers, uiState.users)
        assertEquals(followedUsers, uiState.followedUsers)
        assertTrue(uiState.isLoading)
        assertEquals("Test error", uiState.errorMessage)
    }

    @Test
    fun `UsersUiState copy should work correctly`() {
        // Given
        val originalState = UsersUiState(
            users = emptyList(),
            followedUsers = emptySet(),
            isLoading = true,
            errorMessage = null
        )

        // When
        val newState = originalState.copy(
            isLoading = false,
            errorMessage = "New error"
        )

        // Then
        assertEquals(originalState.users, newState.users)
        assertEquals(originalState.followedUsers, newState.followedUsers)
        assertFalse(newState.isLoading)
        assertEquals("New error", newState.errorMessage)
    }

    @Test
    fun `UsersUiState equality should work correctly`() {
        // Given
        val state1 = UsersUiState(
            users = emptyList(),
            followedUsers = setOf(1, 2),
            isLoading = false,
            errorMessage = "Error"
        )
        val state2 = UsersUiState(
            users = emptyList(),
            followedUsers = setOf(1, 2),
            isLoading = false,
            errorMessage = "Error"
        )
        val state3 = UsersUiState(
            users = emptyList(),
            followedUsers = setOf(1, 2),
            isLoading = true, // Different value
            errorMessage = "Error"
        )

        // Then
        assertEquals(state1, state2)
        assertEquals(state1.hashCode(), state2.hashCode())
        assertNotEquals(state1, state3)
    }
}
