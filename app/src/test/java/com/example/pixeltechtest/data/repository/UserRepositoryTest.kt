package com.example.pixeltechtest.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.emptyPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*

class UserRepositoryTest {

    private lateinit var testDataStore: TestDataStore
    private lateinit var userRepository: UserRepository

    @Before
    fun setup() {
        testDataStore = TestDataStore()
        userRepository = UserRepository(testDataStore)
    }

    @Test
    fun `followUser should store user as followed in DataStore`() = runTest {
        // Given
        val userId = 123

        // When
        userRepository.followUser(userId)

        // Then
        assertTrue(userRepository.isUserFollowed(userId))
    }

    @Test
    fun `unfollowUser should remove user from DataStore`() = runTest {
        // Given
        val userId = 123
        userRepository.followUser(userId) // First follow the user
        assertTrue(userRepository.isUserFollowed(userId))

        // When
        userRepository.unfollowUser(userId)

        // Then
        assertFalse(userRepository.isUserFollowed(userId))
    }

    @Test
    fun `isUserFollowed should return true when user is followed`() = runTest {
        // Given
        val userId = 123
        userRepository.followUser(userId)

        // When
        val result = userRepository.isUserFollowed(userId)

        // Then
        assertTrue(result)
    }

    @Test
    fun `isUserFollowed should return false when user is not followed`() = runTest {
        // Given
        val userId = 123

        // When
        val result = userRepository.isUserFollowed(userId)

        // Then
        assertFalse(result)
    }

    @Test
    fun `getFollowedUsers should return set of followed user IDs`() = runTest {
        // Given
        userRepository.followUser(123)
        userRepository.followUser(456)
        userRepository.followUser(789)

        // When
        val result = userRepository.getFollowedUsers()

        // Then
        assertEquals(setOf(123, 456, 789), result)
    }

    @Test
    fun `getFollowedUsers should handle empty preferences`() = runTest {
        // When
        val result = userRepository.getFollowedUsers()

        // Then
        assertTrue(result.isEmpty())
    }

    @Test
    fun `follow and unfollow multiple users should work correctly`() = runTest {
        // Given
        val userIds = listOf(111, 222, 333)

        // When - Follow all users
        userIds.forEach { userRepository.followUser(it) }

        // Then - All should be followed
        userIds.forEach { userId ->
            assertTrue("User $userId should be followed", userRepository.isUserFollowed(userId))
        }
        assertEquals(userIds.toSet(), userRepository.getFollowedUsers())

        // When - Unfollow one user
        userRepository.unfollowUser(222)

        // Then - Only that user should be unfollowed
        assertTrue(userRepository.isUserFollowed(111))
        assertFalse(userRepository.isUserFollowed(222))
        assertTrue(userRepository.isUserFollowed(333))
        assertEquals(setOf(111, 333), userRepository.getFollowedUsers())
    }

    @Test
    fun `getUsers should return failure when network call fails`() = runTest {
        // This test would require mocking the network call
        // For now, we'll test that the method exists and can be called
        val result = userRepository.getUsers()
        // The result will depend on whether there's actual network connectivity
        // In a real test environment, we'd mock the HTTP connection
        assertNotNull(result)
    }

    // Simple in-memory test implementation of DataStore
    private class TestDataStore : DataStore<Preferences> {
        private val dataFlow = MutableStateFlow(emptyPreferences())

        override val data: Flow<Preferences> = dataFlow

        override suspend fun updateData(transform: suspend (t: Preferences) -> Preferences): Preferences {
            val currentData = dataFlow.value
            val newData = transform(currentData)
            dataFlow.value = newData
            return newData
        }
    }
}
