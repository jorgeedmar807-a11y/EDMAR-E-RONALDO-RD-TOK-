package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.FileDownload
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.BrightWhite
import com.example.ui.theme.CinemaBlack
import com.example.ui.theme.DeepBlack
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.ElegantGrey
import com.example.ui.theme.GlassBorder
import com.example.ui.theme.TechPurple
import com.example.ui.theme.VibrantRed

enum class NavTab {
    HOME,
    DISCOVER,
    CREATE,
    OFFLINE,
    PROFILE
}

@Composable
fun BottomNavBar(
    selectedTab: NavTab,
    offlineCount: Int,
    onTabSelected: (NavTab) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(DeepBlack.copy(alpha = 0.95f))
            .border(
                width = 1.dp,
                brush = Brush.verticalGradient(
                    listOf(GlassBorder, Color.Transparent)
                ),
                shape = RoundedCornerShape(0.dp)
            )
            .navigationBarsPadding()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 6.dp, horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 1. HOME
            NavItem(
                icon = if (selectedTab == NavTab.HOME) Icons.Filled.Home else Icons.Outlined.Home,
                label = "Início",
                isSelected = selectedTab == NavTab.HOME,
                testTag = "nav_home",
                onClick = { onTabSelected(NavTab.HOME) }
            )

            // 2. DISCOVER
            NavItem(
                icon = if (selectedTab == NavTab.DISCOVER) Icons.Filled.Search else Icons.Outlined.Search,
                label = "Descobrir",
                isSelected = selectedTab == NavTab.DISCOVER,
                testTag = "nav_discover",
                onClick = { onTabSelected(NavTab.DISCOVER) }
            )

            // 3. CREATE (Glowing cyber button)
            CreateButton(
                testTag = "nav_create",
                onClick = { onTabSelected(NavTab.CREATE) }
            )

            // 4. OFFLINE (Os Meus 20 Vídeos)
            BadgedBox(
                badge = {
                    if (offlineCount > 0) {
                        Badge(
                            containerColor = ElectricBlue,
                            contentColor = DeepBlack,
                            modifier = Modifier.padding(top = 4.dp, end = 2.dp)
                        ) {
                            Text(text = "$offlineCount")
                        }
                    }
                }
            ) {
                NavItem(
                    icon = if (selectedTab == NavTab.OFFLINE) Icons.Filled.FileDownload else Icons.Outlined.FileDownload,
                    label = "Offline",
                    isSelected = selectedTab == NavTab.OFFLINE,
                    testTag = "nav_offline",
                    onClick = { onTabSelected(NavTab.OFFLINE) }
                )
            }

            // 5. PROFILE
            NavItem(
                icon = if (selectedTab == NavTab.PROFILE) Icons.Filled.Person else Icons.Outlined.Person,
                label = "Perfil",
                isSelected = selectedTab == NavTab.PROFILE,
                testTag = "nav_profile",
                onClick = { onTabSelected(NavTab.PROFILE) }
            )
        }
    }
}

@Composable
private fun NavItem(
    icon: ImageVector,
    label: String,
    isSelected: Boolean,
    testTag: String,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .clickable { onClick() }
            .padding(horizontal = 10.dp, vertical = 4.dp)
            .testTag(testTag)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = if (isSelected) BrightWhite else ElegantGrey,
            modifier = Modifier.size(26.dp)
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color = if (isSelected) BrightWhite else ElegantGrey
        )
    }
}

@Composable
private fun CreateButton(
    testTag: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .width(52.dp)
            .height(34.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(
                Brush.horizontalGradient(
                    colors = listOf(ElectricBlue, TechPurple, VibrantRed)
                )
            )
            .clickable { onClick() }
            .testTag(testTag),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .width(44.dp)
                .height(30.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(DeepBlack),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Criar Vídeo",
                tint = BrightWhite,
                modifier = Modifier.size(22.dp)
            )
        }
    }
}
