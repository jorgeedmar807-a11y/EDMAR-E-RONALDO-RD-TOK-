package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.BrightWhite
import com.example.ui.theme.DeepBlack
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.ElegantGrey
import com.example.ui.theme.GoldCrown
import com.example.ui.theme.VibrantRed

@Composable
fun TopCategoryBar(
    categories: List<String>,
    selectedCategory: String,
    unreadMessagesCount: Int,
    unreadNotificationsCount: Int,
    isOnline: Boolean,
    onCategorySelected: (String) -> Unit,
    onSearchClick: () -> Unit,
    onMessagesClick: () -> Unit,
    onNotificationsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(
                    0.0f to DeepBlack.copy(alpha = 0.85f),
                    0.7f to DeepBlack.copy(alpha = 0.4f),
                    1.0f to Color.Transparent
                )
            )
            .padding(top = 36.dp, bottom = 4.dp)
    ) {
        // App Title Row with Brand & Actions
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Logo & Brand
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Crown icon
                Text(
                    text = "👑",
                    fontSize = 20.sp,
                    modifier = Modifier.padding(end = 4.dp)
                )
                Text(
                    text = "ER TOK",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = BrightWhite
                )

                Spacer(modifier = Modifier.width(6.dp))

                // Online/Offline status pill
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (isOnline) Color(0x3300C853) else Color(0x33FF1744))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = if (isOnline) "ONLINE" else "OFFLINE",
                        color = if (isOnline) Color(0xFF00E676) else VibrantRed,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Action Icons (Search, Notifications, Messages)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                IconButton(
                    onClick = onSearchClick,
                    modifier = Modifier.testTag("top_search_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Pesquisar",
                        tint = BrightWhite,
                        modifier = Modifier.size(24.dp)
                    )
                }

                IconButton(
                    onClick = onNotificationsClick,
                    modifier = Modifier.testTag("top_notifications_button")
                ) {
                    BadgedBox(
                        badge = {
                            if (unreadNotificationsCount > 0) {
                                Badge(
                                    containerColor = VibrantRed,
                                    contentColor = BrightWhite
                                ) {
                                    Text(text = "$unreadNotificationsCount")
                                }
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.NotificationsNone,
                            contentDescription = "Notificações",
                            tint = BrightWhite,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }

                IconButton(
                    onClick = onMessagesClick,
                    modifier = Modifier.testTag("top_messages_button")
                ) {
                    BadgedBox(
                        badge = {
                            if (unreadMessagesCount > 0) {
                                Badge(
                                    containerColor = ElectricBlue,
                                    contentColor = DeepBlack
                                ) {
                                    Text(text = "$unreadMessagesCount")
                                }
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.MailOutline,
                            contentDescription = "Mensagens",
                            tint = BrightWhite,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Horizontal scrollable categories
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(scrollState)
                .padding(horizontal = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            categories.forEach { category ->
                val isSelected = category == selectedCategory
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .clickable { onCategorySelected(category) }
                        .padding(vertical = 4.dp)
                        .testTag("category_tab_$category")
                ) {
                    Text(
                        text = category,
                        fontSize = if (isSelected) 16.sp else 14.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = if (isSelected) BrightWhite else ElegantGrey.copy(alpha = 0.8f)
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    if (isSelected) {
                        Box(
                            modifier = Modifier
                                .width(24.dp)
                                .height(3.dp)
                                .clip(RoundedCornerShape(2.dp))
                                .background(ElectricBlue)
                        )
                    } else {
                        Spacer(modifier = Modifier.height(3.dp))
                    }
                }
            }
        }
    }
}
