package com.example.retrofitgithubapp.ui.viewmodel

import com.example.retrofitgithubapp.data.model.Repository

/**
 * Represents the distinct UI states for the repository screen.
 */
sealed interface RepoUiState {
    data object Idle : RepoUiState
    data class Loading(val username: String) : RepoUiState
    data class Success(val username: String, val repositories: List<Repository>) : RepoUiState
    data class Empty(val username: String) : RepoUiState
    data class Error(val username: String, val message: String) : RepoUiState
}
