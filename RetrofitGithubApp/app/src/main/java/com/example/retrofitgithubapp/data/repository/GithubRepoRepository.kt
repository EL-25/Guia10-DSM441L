package com.example.retrofitgithubapp.data.repository

import com.example.retrofitgithubapp.data.api.GithubApiService
import com.example.retrofitgithubapp.data.api.RetrofitClient
import com.example.retrofitgithubapp.data.model.Repository
import java.io.IOException

/**
 * Repository layer that encapsulates data access from GitHub REST API.
 */
class GithubRepoRepository(
    private val apiService: GithubApiService = RetrofitClient.apiService
) {

    suspend fun getUserRepositories(username: String): Result<List<Repository>> {
        return try {
            val response = apiService.getUserRepositories(username.trim())
            if (response.isSuccessful) {
                val repos = response.body() ?: emptyList()
                Result.success(repos)
            } else {
                val errorMessage = when (response.code()) {
                    404 -> "No se encontró el usuario '$username' en GitHub."
                    403 -> "Límite de solicitudes de la API de GitHub alcanzado. Intenta de nuevo en unos minutos."
                    else -> "Error en el servidor: Código ${response.code()}"
                }
                Result.failure(Exception(errorMessage))
            }
        } catch (e: IOException) {
            Result.failure(Exception("Error de conexión. Verifica tu acceso a internet."))
        } catch (e: Exception) {
            Result.failure(Exception(e.localizedMessage ?: "Ocurrió un error inesperado al consultar los repositorios."))
        }
    }
}
