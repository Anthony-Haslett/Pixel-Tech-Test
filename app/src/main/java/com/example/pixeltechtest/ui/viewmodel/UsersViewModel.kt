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
    val users: List<User> = emptyList(),
    val followedUsers: Set<Int> = emptySet(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class UsersViewModel(private val userRepository: UserRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(UsersUiState())
    val uiState: StateFlow<UsersUiState> = _uiState.asStateFlow()

    init {
        loadUsers()
    }

    fun loadUsers() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                errorMessage = null
            )

            userRepository.getUsers().fold(
                onSuccess = { users ->
                    _uiState.value = _uiState.value.copy(
                        users = users,
                        followedUsers = userRepository.getFollowedUsers(),
                        isLoading = false
                    )
                },
                onFailure = { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = error.message ?: "Unknown error occurred"
                    )
                }
            )
        }
    }

    fun toggleFollow(userId: Int) {
        val isCurrentlyFollowed = _uiState.value.followedUsers.contains(userId)

        if (isCurrentlyFollowed) {
            userRepository.unfollowUser(userId)
        } else {
            userRepository.followUser(userId)
        }

        _uiState.value = _uiState.value.copy(
            followedUsers = userRepository.getFollowedUsers()
        )
    }

    fun retry() {
        loadUsers()
    }
}
