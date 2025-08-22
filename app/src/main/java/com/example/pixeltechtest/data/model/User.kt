package com.example.pixeltechtest.data.model

data class User(
    val userId: Int,
    val displayName: String,
    val reputation: Int,
    val profileImage: String,
    val location: String?,
    val websiteUrl: String?,
    val link: String,
    val badgeCounts: BadgeCounts,
    val isEmployee: Boolean,
    val userType: String,
    val acceptRate: Int?,
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
