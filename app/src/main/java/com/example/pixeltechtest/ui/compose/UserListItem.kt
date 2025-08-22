package com.example.pixeltechtest.ui.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pixeltechtest.data.model.User
import java.text.NumberFormat

@Composable
fun UserListItem(
    user: User,
    ranking: Int,
    isFollowed: Boolean,
    onFollowToggle: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Ranking Number
            RankingBadge(
                ranking = ranking,
                modifier = Modifier.padding(end = 16.dp)
            )

            // Profile Image Placeholder
            UserProfileImage(
                displayName = user.displayName,
                modifier = Modifier.size(56.dp)
            )

            Spacer(modifier = Modifier.width(16.dp))

            // User Info
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = user.displayName,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Reputation: ${NumberFormat.getNumberInstance().format(user.reputation)}",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                if (user.location != null) {
                    Text(
                        text = user.location,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Follow Button
            FollowButton(
                isFollowed = isFollowed,
                onFollowToggle = { onFollowToggle(user.userId) }
            )
        }
    }
}

@Composable
fun RankingBadge(
    ranking: Int,
    modifier: Modifier = Modifier
) {
    val badgeColor = when (ranking) {
        1 -> MaterialTheme.colorScheme.tertiary // Gold-ish for #1
        in 2..3 -> MaterialTheme.colorScheme.secondary // Silver-ish for top 3
        else -> MaterialTheme.colorScheme.surfaceVariant // Default for others
    }

    val textColor = when (ranking) {
        1 -> MaterialTheme.colorScheme.onTertiary
        in 2..3 -> MaterialTheme.colorScheme.onSecondary
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }

    Surface(
        modifier = modifier
            .size(36.dp),
        shape = CircleShape,
        color = badgeColor
    ) {
        Box(
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = ranking.toString(),
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = textColor
            )
        }
    }
}

@Composable
fun UserProfileImage(
    displayName: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.primaryContainer)
            .border(2.dp, MaterialTheme.colorScheme.outline, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        // Simple placeholder using first letter of display name
        Text(
            text = displayName.firstOrNull()?.toString()?.uppercase() ?: "?",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
    }
}

@Composable
fun FollowButton(
    isFollowed: Boolean,
    onFollowToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onFollowToggle,
        modifier = modifier,
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isFollowed)
                MaterialTheme.colorScheme.secondary
            else
                MaterialTheme.colorScheme.primary
        ),
        shape = RoundedCornerShape(20.dp)
    ) {
        Text(
            text = if (isFollowed) "Unfollow" else "Follow",
            fontSize = 12.sp
        )
    }
}
