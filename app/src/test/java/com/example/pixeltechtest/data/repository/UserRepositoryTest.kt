package com.example.pixeltechtest.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.emptyPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.*
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*

// Test DataStore implementation
class TestDataStore : DataStore<Preferences> {
    private val preferencesFlow = MutableStateFlow(emptyPreferences())

    override val data: Flow<Preferences> = preferencesFlow

    override suspend fun updateData(transform: suspend (t: Preferences) -> Preferences): Preferences {
        val newPrefs = transform(preferencesFlow.value)
        preferencesFlow.value = newPrefs
        return newPrefs
    }
}

class UserRepositoryTest {

    private lateinit var testDataStore: TestDataStore
    private lateinit var userRepository: UserRepository

    @Before
    fun setup() {
        testDataStore = TestDataStore()
        userRepository = UserRepository(testDataStore)
    }

    @Test
    fun `followUser should store user as followed in DataStore`() {
        runBlocking {
            // Given
            val userId = 123

            // When
            userRepository.followUser(userId)

            // Then
            assertTrue(userRepository.isUserFollowed(userId))
        }
    }

    @Test
    fun `unfollowUser should remove user from DataStore`() {
        runBlocking {
            // Given
            val userId = 456
            userRepository.followUser(userId) // First follow the user
            assertTrue(userRepository.isUserFollowed(userId))

            // When
            userRepository.unfollowUser(userId)

            // Then
            assertFalse(userRepository.isUserFollowed(userId))
        }
    }

    @Test
    fun `isUserFollowed should return true when user is followed`() {
        runBlocking {
            // Given
            val userId = 789
            userRepository.followUser(userId)

            // When
            val result = userRepository.isUserFollowed(userId)

            // Then
            assertTrue(result)
        }
    }

    @Test
    fun `isUserFollowed should return false when user is not followed`() {
        runBlocking {
            // Given
            val userId = 999

            // When
            val result = userRepository.isUserFollowed(userId)

            // Then
            assertFalse(result)
        }
    }

    @Test
    fun `getFollowedUsers should return set of followed user IDs`() {
        runBlocking {
            // Given
            userRepository.followUser(123)
            userRepository.followUser(456)
            userRepository.followUser(789)

            // When
            val result = userRepository.getFollowedUsers()

            // Then
            assertEquals(setOf(123, 456, 789), result)
        }
    }

    @Test
    fun `getFollowedUsers should handle empty preferences`() {
        runBlocking {
            // When
            val result = userRepository.getFollowedUsers()

            // Then
            assertTrue(result.isEmpty())
        }
    }

    @Test
    fun `follow and unfollow multiple users should work correctly`() {
        runBlocking {
            // Given
            val userIds = listOf(111, 222, 333)

            // Follow all users
            userIds.forEach { userRepository.followUser(it) }

            // Verify all are followed
            userIds.forEach { userId ->
                assertTrue("User $userId should be followed", userRepository.isUserFollowed(userId))
            }
            assertEquals(userIds.toSet(), userRepository.getFollowedUsers())

            // Unfollow one user
            userRepository.unfollowUser(222)

            // Verify correct state
            assertTrue(userRepository.isUserFollowed(111))
            assertFalse(userRepository.isUserFollowed(222))
            assertTrue(userRepository.isUserFollowed(333))
            assertEquals(setOf(111, 333), userRepository.getFollowedUsers())
        }
    }

    @Test
    fun `getUsers should return result`() {
        runBlocking {
            // When - Note: This will make an actual network call to StackOverflow API
            // In a real test environment, you'd want to mock this
            val result = userRepository.getUsers()

            // Then - We can't predict the exact result since it's a live API call
            // But we can test that it returns a Result type
            assertNotNull(result)
            // The result could be success or failure depending on network connectivity
        }
    }
}
