package com.example.retrofitgithubapp.data.api

import com.example.retrofitgithubapp.data.model.Repository
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Retrofit interface defining the GitHub API endpoints.
 * Endpoint required: https://api.github.com/users/{username}/repos
 */
interface GithubApiService {

    @GET("users/{username}/repos")
    suspend fun getUserRepositories(
        @Path("username") username: String,
        @Query("sort") sort: String = "updated",
        @Query("per_page") perPage: Int = 100
    ): Response<List<Repository>>
}
