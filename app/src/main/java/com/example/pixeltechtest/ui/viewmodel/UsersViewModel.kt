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
    val error: String? = null,
    val followedUsers: Set<Int> = emptySet()
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
                val currentFollowed = _uiState.value.followedUsers
                val newFollowed = if (currentFollowed.contains(userId)) {
                    userRepository.unfollowUser(userId)
                    currentFollowed - userId
                } else {
                    userRepository.followUser(userId)
                    currentFollowed + userId
                }

                _uiState.value = _uiState.value.copy(followedUsers = newFollowed)
            } catch (e: Exception) {
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
}
