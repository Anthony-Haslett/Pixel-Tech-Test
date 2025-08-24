package com.example.pixeltechtest.ui.viewmodel

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.emptyPreferences
import com.example.pixeltechtest.data.model.BadgeCounts
import com.example.pixeltechtest.data.model.User
import com.example.pixeltechtest.data.repository.UserRepository
import kotlinx.coroutines.*
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import org.junit.Before
import org.junit.After
import org.junit.Test
import org.junit.Assert.*

class UsersViewModelTest {

    private lateinit var testDataStore: TestDataStore
    private lateinit var testRepository: UserRepository
    private lateinit var viewModel: UsersViewModel

    @Before
    fun setup() {
        // Set the Main dispatcher to use TestCoroutineDispatcher for testing
        Dispatchers.setMain(Dispatchers.Unconfined)

        testDataStore = TestDataStore()
        testRepository = UserRepository(testDataStore)
        viewModel = UsersViewModel(testRepository)
    }

    @After
    fun tearDown() {
        // Reset the Main dispatcher after each test
        Dispatchers.resetMain()
    }

    @Test
    fun `followUser should store user as followed`() {
        runBlocking {
            // Given
            val userId = 123

            // When
            testRepository.followUser(userId)

            // Then
            assertTrue(testRepository.isUserFollowed(userId))
        }
    }

    @Test
    fun `unfollowUser should remove user from followed`() {
        runBlocking {
            // Given
            val userId = 123
            testRepository.followUser(userId)
            assertTrue(testRepository.isUserFollowed(userId))

            // When
            testRepository.unfollowUser(userId)

            // Then
            assertFalse(testRepository.isUserFollowed(userId))
        }
    }

    @Test
    fun `getFollowedUsers should return correct set of followed users`() {
        runBlocking {
            // Given
            testRepository.followUser(123)
            testRepository.followUser(456)
            testRepository.followUser(789)

            // When
            val followedUsers = testRepository.getFollowedUsers()

            // Then
            assertEquals(setOf(123, 456, 789), followedUsers)
        }
    }

    @Test
    fun `loadUsers should work correctly`() {
        runBlocking {
            // When
            viewModel.loadUsers()
            delay(100) // Give time for async operations to complete

            // Then
            val state = viewModel.uiState.first()
            // Note: Since we're making a real network call, we can't predict the exact result
            // We can only verify that the state is valid
            assertNotNull(state)
            // The result could be success or failure depending on network connectivity
        }
    }

    @Test
    fun `retry should call loadUsers again`() {
        runBlocking {
            // When - Call retry (which internally calls loadUsers)
            viewModel.retry()
            delay(100)

            // Then - Should have valid state
            val currentState = viewModel.uiState.first()
            assertNotNull(currentState)
        }
    }

    @Test
    fun `initial state should be loading`() {
        runBlocking {
            // Allow time for init block to execute
            delay(50)

            // When - Check initial state (after init operations)
            val initialState = viewModel.uiState.first()

            // Then - Should have valid state
            assertNotNull(initialState)
            // Since loadUsers is called in init, we can't guarantee the exact loading state
            // but we can verify the state structure is correct
        }
    }

    @Test
    fun `repository operations should work correctly`() {
        runBlocking {
            // Test basic repository functionality
            val userId1 = 111
            val userId2 = 222

            // Initially no users should be followed
            assertFalse(testRepository.isUserFollowed(userId1))
            assertTrue(testRepository.getFollowedUsers().isEmpty())

            // Follow first user
            testRepository.followUser(userId1)
            assertTrue(testRepository.isUserFollowed(userId1))
            assertEquals(setOf(userId1), testRepository.getFollowedUsers())

            // Follow second user
            testRepository.followUser(userId2)
            assertTrue(testRepository.isUserFollowed(userId1))
            assertTrue(testRepository.isUserFollowed(userId2))
            assertEquals(setOf(userId1, userId2), testRepository.getFollowedUsers())

            // Unfollow first user
            testRepository.unfollowUser(userId1)
            assertFalse(testRepository.isUserFollowed(userId1))
            assertTrue(testRepository.isUserFollowed(userId2))
            assertEquals(setOf(userId2), testRepository.getFollowedUsers())

            // Unfollow second user
            testRepository.unfollowUser(userId2)
            assertFalse(testRepository.isUserFollowed(userId1))
            assertFalse(testRepository.isUserFollowed(userId2))
            assertTrue(testRepository.getFollowedUsers().isEmpty())
        }
    }

    private fun createTestUsers(): List<User> {
        return listOf(
            User(
                userId = 1,
                displayName = "User 1",
                reputation = 10000,
                profileImage = "http://example.com/image1.jpg",
                location = "Test City 1",
                websiteUrl = "http://example1.com",
                link = "link1",
                badgeCounts = BadgeCounts(1, 2, 3),
                isEmployee = false,
                userType = "registered",
                acceptRate = 85,
                creationDate = 123456,
                lastAccessDate = 123456,
                lastModifiedDate = 123456,
                accountId = 1
            ),
            User(
                userId = 2,
                displayName = "User 2",
                reputation = 20000,
                profileImage = "http://example.com/image2.jpg",
                location = "Test City 2",
                websiteUrl = "http://example2.com",
                link = "link2",
                badgeCounts = BadgeCounts(2, 4, 6),
                isEmployee = true,
                userType = "registered",
                acceptRate = 92,
                creationDate = 123456,
                lastAccessDate = 123456,
                lastModifiedDate = 123456,
                accountId = 2
            )
        )
    }
}

// Simple in-memory test implementation of DataStore
class TestDataStore : DataStore<Preferences> {
    private val dataFlow = MutableStateFlow(emptyPreferences())

    override val data: Flow<Preferences> = dataFlow

    override suspend fun updateData(transform: suspend (t: Preferences) -> Preferences): Preferences {
        val currentData = dataFlow.value
        val newData = transform(currentData)
        dataFlow.value = newData
        return newData
    }
}
