package com.example.pixeltechtest.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pixeltechtest.data.model.User
import com.example.pixeltechtest.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class UsersUiState(
    val isLoading: Boolean = true,
    val users: List<User> = emptyList(),
    val filteredUsers: List<User> = emptyList(),
    val error: String? = null,
    val followedUsers: Set<Int> = emptySet(),
    val followingInProgress: Set<Int> = emptySet(),
    val searchQuery: String = "",
    val isSearchActive: Boolean = false
)

class UsersViewModel(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(UsersUiState())
    val uiState: StateFlow<UsersUiState> = _uiState.asStateFlow()

    init {
        loadUsers()
        loadFollowedUsers()
    }

    private fun loadFollowedUsers() {
        viewModelScope.launch {
            try {
                val followedUsers = userRepository.getFollowedUsers()
                _uiState.value = _uiState.value.copy(followedUsers = followedUsers)
            } catch (e: Exception) {
                // Handle error silently for followed users loading
            }
        }
    }

    fun loadUsers() {
        _uiState.value = _uiState.value.copy(isLoading = true, error = null)

        viewModelScope.launch {
            val result = userRepository.getUsers()
            result.fold(
                onSuccess = { users ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        users = users,
                        error = null
                    )
                },
                onFailure = { exception ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = exception.message ?: "Unknown error"
                    )
                }
            )
        }
    }

    fun toggleFollowUser(userId: Int) {
        viewModelScope.launch {
            try {
                // Add user to following in progress
                _uiState.value = _uiState.value.copy(
                    followingInProgress = _uiState.value.followingInProgress + userId
                )

                val currentFollowed = _uiState.value.followedUsers
                val newFollowed = if (currentFollowed.contains(userId)) {
                    userRepository.unfollowUser(userId)
                    currentFollowed - userId
                } else {
                    userRepository.followUser(userId)
                    currentFollowed + userId
                }

                _uiState.value = _uiState.value.copy(
                    followedUsers = newFollowed,
                    followingInProgress = _uiState.value.followingInProgress - userId
                )
            } catch (e: Exception) {
                // Remove from progress even on error
                _uiState.value = _uiState.value.copy(
                    followingInProgress = _uiState.value.followingInProgress - userId
                )
                // Handle error - could show a toast or error message
            }
        }
    }

    fun retry() {
        loadUsers()
    }

    fun refreshFollowedUsers() {
        loadFollowedUsers()
    }

    fun updateSearchQuery(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
        filterUsers(query)
    }

    fun setSearchActive(isActive: Boolean) {
        _uiState.value = _uiState.value.copy(isSearchActive = isActive)
        if (!isActive) {
            // Clear search when deactivating
            _uiState.value = _uiState.value.copy(searchQuery = "", filteredUsers = emptyList())
        }
    }

    private fun filterUsers(query: String) {
        val currentUsers = _uiState.value.users
        val filtered = if (query.isBlank()) {
            emptyList()
        } else {
            currentUsers.filter { user ->
                user.displayName.contains(query, ignoreCase = true) ||
                        user.location?.contains(query, ignoreCase = true) == true ||
                        user.reputation.toString().contains(query)
            }
        }
        _uiState.value = _uiState.value.copy(filteredUsers = filtered)
    }
}
