package com.example.pixeltechtest.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.example.pixeltechtest.data.model.BadgeCounts
import com.example.pixeltechtest.data.model.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import androidx.core.content.edit

class UserRepository(context: Context) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("user_follows", Context.MODE_PRIVATE)

    suspend fun getUsers(): Result<List<User>> = withContext(Dispatchers.IO) {
        try {
            val url = URL("https://api.stackexchange.com/2.2/users?page=1&pagesize=20&order=desc&sort=reputation&site=stackoverflow")
            val connection = url.openConnection() as HttpURLConnection

            connection.requestMethod = "GET"
            connection.connectTimeout = 10000
            connection.readTimeout = 10000

            if (connection.responseCode == HttpURLConnection.HTTP_OK) {
                val response = connection.inputStream.bufferedReader().use { it.readText() }
                val users = parseUsersFromJson(response)
                Result.success(users)
            } else {
                Result.failure(IOException("API call failed with response code: ${connection.responseCode}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun parseUsersFromJson(jsonString: String): List<User> {
        val users = mutableListOf<User>()
        try {
            val jsonObject = JSONObject(jsonString)
            val itemsArray = jsonObject.getJSONArray("items")

            for (i in 0 until itemsArray.length()) {
                val userJson = itemsArray.getJSONObject(i)
                val badgeCountsJson = userJson.getJSONObject("badge_counts")

                val user = User(
                    userId = userJson.getInt("user_id"),
                    displayName = userJson.getString("display_name"),
                    reputation = userJson.getInt("reputation"),
                    profileImage = userJson.optString("profile_image").takeIf { it.isNotEmpty() },
                    location = userJson.optString("location").takeIf { it.isNotEmpty() },
                    websiteUrl = userJson.optString("website_url").takeIf { it.isNotEmpty() },
                    link = userJson.getString("link"),
                    badgeCounts = BadgeCounts(
                        bronze = badgeCountsJson.getInt("bronze"),
                        silver = badgeCountsJson.getInt("silver"),
                        gold = badgeCountsJson.getInt("gold")
                    ),
                    isEmployee = userJson.getBoolean("is_employee"),
                    userType = userJson.getString("user_type"),
                    acceptRate = if (userJson.has("accept_rate")) userJson.getInt("accept_rate") else null,
                    creationDate = userJson.getLong("creation_date"),
                    lastAccessDate = userJson.getLong("last_access_date"),
                    lastModifiedDate = userJson.getLong("last_modified_date"),
                    accountId = userJson.getInt("account_id")
                )
                users.add(user)
            }
        } catch (e: Exception) {
            throw IOException("Failed to parse JSON response: ${e.message}")
        }
        return users
    }

    fun followUser(userId: Int) {
        sharedPreferences.edit { putBoolean(userId.toString(), true) }
    }

    fun unfollowUser(userId: Int) {
        sharedPreferences.edit { remove(userId.toString()) }
    }

    fun isUserFollowed(userId: Int): Boolean {
        return sharedPreferences.getBoolean(userId.toString(), false)
    }

    fun getFollowedUsers(): Set<Int> {
        return sharedPreferences.all.keys.mapNotNull { it.toIntOrNull() }.toSet()
    }
}
