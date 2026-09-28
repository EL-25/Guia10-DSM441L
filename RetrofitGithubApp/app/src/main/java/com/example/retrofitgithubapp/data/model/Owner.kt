package com.example.retrofitgithubapp.data.model

import com.google.gson.annotations.SerializedName

data class Owner(
    @SerializedName("id")
    val id: Long,
    @SerializedName("login")
    val login: String,
    @SerializedName("avatar_url")
    val avatarUrl: String?,
    @SerializedName("html_url")
    val htmlUrl: String?
)
