package com.example.pixeltechtest.data.model

data class User(
    val userId: Int,
    val displayName: String,
    val reputation: Int,
    val profileImage: String? = null,
    val location: String? = null,
    val websiteUrl: String? = null,
    val link: String,
    val badgeCounts: BadgeCounts,
    val isEmployee: Boolean,
    val userType: String,
    val acceptRate: Int? = null,
    val creationDate: Long,
    val lastAccessDate: Long,
    val lastModifiedDate: Long,
    val accountId: Int
)

data class BadgeCounts(
    val bronze: Int,
    val silver: Int,
    val gold: Int
)
