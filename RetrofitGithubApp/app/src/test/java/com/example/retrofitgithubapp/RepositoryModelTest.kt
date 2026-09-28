package com.example.retrofitgithubapp

import com.example.retrofitgithubapp.data.model.Repository
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Test

/**
 * Unit test to verify serialization and deserialization of the GitHub repository JSON response.
 */
class RepositoryModelTest {

    @Test
    fun parseGithubReposJsonResponse_success() {
        val sampleJson = """
        [
          {
            "id": 1,
            "node_id": "MDEwOlJlcG9zaXRvcnkx",
            "name": "grit",
            "full_name": "mojombo/grit",
            "owner": {
              "login": "mojombo",
              "id": 1,
              "node_id": "MDQ6VXNlcjE=",
              "avatar_url": "https://avatars.githubusercontent.com/u/1?v=4",
              "html_url": "https://github.com/mojombo"
            },
            "private": false,
            "html_url": "https://github.com/mojombo/grit",
            "description": "**Grit is no longer maintained. Check out libgit2/rugged.**",
            "fork": false,
            "stargazers_count": 1968,
            "forks_count": 535,
            "language": "Ruby",
            "updated_at": "2024-03-24T12:00:00Z"
          }
        ]
        """.trimIndent()

        val gson = Gson()
        val listType = object : TypeToken<List<Repository>>() {}.type
        val repos: List<Repository> = gson.fromJson(sampleJson, listType)

        assertNotNull(repos)
        assertEquals(1, repos.size)

        val repo = repos[0]
        assertEquals(1L, repo.id)
        assertEquals("grit", repo.name)
        assertEquals("mojombo/grit", repo.fullName)
        assertEquals("mojombo", repo.owner.login)
        assertFalse(repo.isPrivate)
        assertFalse(repo.isFork)
        assertEquals("Ruby", repo.language)
        assertEquals(1968, repo.stargazersCount)
        assertEquals(535, repo.forksCount)
        assertEquals("https://github.com/mojombo/grit", repo.htmlUrl)
    }
}
