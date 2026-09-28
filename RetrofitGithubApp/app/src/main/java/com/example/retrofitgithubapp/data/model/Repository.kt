package com.example.retrofitgithubapp.data.model

import com.google.gson.annotations.SerializedName

/**
 * Data class representing a GitHub Repository returned by:
 * GET https://api.github.com/users/{username}/repos
 */
data class Repository(
    @SerializedName("id")
    val id: Long,

    @SerializedName("node_id")
    val nodeId: String?,

    @SerializedName("name")
    val name: String,

    @SerializedName("full_name")
    val fullName: String,

    @SerializedName("owner")
    val owner: Owner,

    @SerializedName("private")
    val isPrivate: Boolean,

    @SerializedName("html_url")
    val htmlUrl: String,

    @SerializedName("description")
    val description: String?,

    @SerializedName("fork")
    val isFork: Boolean,

    @SerializedName("stargazers_count")
    val stargazersCount: Int = 0,

    @SerializedName("forks_count")
    val forksCount: Int = 0,

    @SerializedName("language")
    val language: String?,

    @SerializedName("updated_at")
    val updatedAt: String?
)
