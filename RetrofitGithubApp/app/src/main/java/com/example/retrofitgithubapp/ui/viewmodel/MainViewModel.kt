package com.example.retrofitgithubapp.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.retrofitgithubapp.data.repository.GithubRepoRepository
import kotlinx.coroutines.launch

/**
 * ViewModel responsible for managing repository search logic and UI state.
 */
class MainViewModel(
    private val repository: GithubRepoRepository = GithubRepoRepository()
) : ViewModel() {

    private val _uiState = MutableLiveData<RepoUiState>(RepoUiState.Idle)
    val uiState: LiveData<RepoUiState> = _uiState

    var currentUsername: String = ""
        private set

    /**
     * Executes repository search for a given GitHub username.
     */
    fun searchRepositories(username: String) {
        val trimmed = username.trim()
        if (trimmed.isBlank()) {
            _uiState.value = RepoUiState.Error("", "Por favor, escribe un nombre de usuario.")
            return
        }

        currentUsername = trimmed
        _uiState.value = RepoUiState.Loading(trimmed)

        viewModelScope.launch {
            val result = repository.getUserRepositories(trimmed)
            result.onSuccess { repos ->
                if (repos.isEmpty()) {
                    _uiState.value = RepoUiState.Empty(trimmed)
                } else {
                    _uiState.value = RepoUiState.Success(trimmed, repos)
                }
            }.onFailure { error ->
                _uiState.value = RepoUiState.Error(
                    trimmed,
                    error.message ?: "Ocurrió un error al cargar los repositorios."
                )
            }
        }
    }

    /**
     * Retries search with the last requested username.
     */
    fun retry() {
        if (currentUsername.isNotBlank()) {
            searchRepositories(currentUsername)
        }
    }
}
