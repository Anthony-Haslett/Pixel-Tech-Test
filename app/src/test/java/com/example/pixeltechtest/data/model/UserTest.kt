package com.example.pixeltechtest.data.model

import org.junit.Test
import org.junit.Assert.*

class UserTest {

    @Test
    fun `User data class should create instance correctly`() {
        // Given
        val badgeCounts = BadgeCounts(bronze = 10, silver = 5, gold = 2)
        val user = User(
            userId = 123,
            displayName = "Test User",
            reputation = 50000,
            profileImage = "http://example.com/image.jpg",
            location = "Test City",
            websiteUrl = "http://example.com",
            link = "http://stackoverflow.com/users/123",
            badgeCounts = badgeCounts,
            isEmployee = false,
            userType = "registered",
            acceptRate = 85,
            creationDate = 1234567890,
            lastAccessDate = 1234567900,
            lastModifiedDate = 1234567895,
            accountId = 456
        )

        // Then
        assertEquals(123, user.userId)
        assertEquals("Test User", user.displayName)
        assertEquals(50000, user.reputation)
        assertEquals("http://example.com/image.jpg", user.profileImage)
        assertEquals("Test City", user.location)
        assertEquals("http://example.com", user.websiteUrl)
        assertEquals("http://stackoverflow.com/users/123", user.link)
        assertEquals(badgeCounts, user.badgeCounts)
        assertFalse(user.isEmployee)
        assertEquals("registered", user.userType)
        assertEquals(85, user.acceptRate)
        assertEquals(1234567890, user.creationDate)
        assertEquals(1234567900, user.lastAccessDate)
        assertEquals(1234567895, user.lastModifiedDate)
        assertEquals(456, user.accountId)
    }

    @Test
    fun `User with null optional fields should work correctly`() {
        // Given
        val badgeCounts = BadgeCounts(bronze = 0, silver = 0, gold = 0)
        val user = User(
            userId = 123,
            displayName = "Test User",
            reputation = 1000,
            profileImage = "http://example.com/default.jpg", // profileImage is non-nullable
            location = null,
            websiteUrl = null,
            link = "http://stackoverflow.com/users/123",
            badgeCounts = badgeCounts,
            isEmployee = false,
            userType = "registered",
            acceptRate = null,
            creationDate = 1234567890,
            lastAccessDate = 1234567900,
            lastModifiedDate = 1234567895,
            accountId = 456
        )

        // Then
        assertEquals("http://example.com/default.jpg", user.profileImage) // profileImage is non-nullable
        assertNull(user.location)
        assertNull(user.websiteUrl)
        assertNull(user.acceptRate)
        // Other fields should still be set
        assertEquals(123, user.userId)
        assertEquals("Test User", user.displayName)
    }

    @Test
    fun `BadgeCounts should create instance correctly`() {
        // Given
        val badgeCounts = BadgeCounts(bronze = 15, silver = 8, gold = 3)

        // Then
        assertEquals(15, badgeCounts.bronze)
        assertEquals(8, badgeCounts.silver)
        assertEquals(3, badgeCounts.gold)
    }

    @Test
    fun `BadgeCounts with zero values should work correctly`() {
        // Given
        val badgeCounts = BadgeCounts(bronze = 0, silver = 0, gold = 0)

        // Then
        assertEquals(0, badgeCounts.bronze)
        assertEquals(0, badgeCounts.silver)
        assertEquals(0, badgeCounts.gold)
    }

    @Test
    fun `User equality should work correctly`() {
        // Given
        val badgeCounts1 = BadgeCounts(bronze = 10, silver = 5, gold = 2)
        val badgeCounts2 = BadgeCounts(bronze = 10, silver = 5, gold = 2)

        val user1 = User(
            userId = 123,
            displayName = "Test User",
            reputation = 50000,
            profileImage = "http://example.com/image.jpg",
            location = "Test City",
            websiteUrl = "http://example.com",
            link = "http://stackoverflow.com/users/123",
            badgeCounts = badgeCounts1,
            isEmployee = false,
            userType = "registered",
            acceptRate = 85,
            creationDate = 1234567890,
            lastAccessDate = 1234567900,
            lastModifiedDate = 1234567895,
            accountId = 456
        )

        val user2 = User(
            userId = 123,
            displayName = "Test User",
            reputation = 50000,
            profileImage = "http://example.com/image.jpg",
            location = "Test City",
            websiteUrl = "http://example.com",
            link = "http://stackoverflow.com/users/123",
            badgeCounts = badgeCounts2,
            isEmployee = false,
            userType = "registered",
            acceptRate = 85,
            creationDate = 1234567890,
            lastAccessDate = 1234567900,
            lastModifiedDate = 1234567895,
            accountId = 456
        )

        // Then
        assertEquals(user1, user2)
        assertEquals(user1.hashCode(), user2.hashCode())
    }

    @Test
    fun `BadgeCounts equality should work correctly`() {
        // Given
        val badges1 = BadgeCounts(bronze = 10, silver = 5, gold = 2)
        val badges2 = BadgeCounts(bronze = 10, silver = 5, gold = 2)
        val badges3 = BadgeCounts(bronze = 11, silver = 5, gold = 2)

        // Then
        assertEquals(badges1, badges2)
        assertEquals(badges1.hashCode(), badges2.hashCode())
        assertNotEquals(badges1, badges3)
    }
}
