package com.example.avito.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.avito.components.PlaybackBarStatus
import com.example.avito.player.PlayerViewModel
import com.example.avito.viewmodel.DownloadedTracksViewModel

@Composable
fun BottomNavigationBar(
    navController: NavController,
    modifier: Modifier = Modifier,
) {
    NavigationBar(modifier = modifier, containerColor = Color.Unspecified) {
        val backStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = backStackEntry?.destination?.route
        NavBarItems.BarItems.forEach { navItem ->
            NavigationBarItem(
                selected = currentRoute == navItem.route,
                onClick = {
                    if (currentRoute != navItem.route) {
                        navController.navigate(navItem.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                icon = {
                    Icon(
                        painter = painterResource(id = navItem.icon),
                        contentDescription = navItem.title,
                        modifier = Modifier
                            .size(20.dp)
                    )
                },
                label = {
                    Text(
                        text = navItem.title,
                        fontSize = 9.sp,
                        maxLines = 1,
                        lineHeight = 5.sp,
                        overflow = TextOverflow.Ellipsis
                    )
                },
            )
        }
    }
}