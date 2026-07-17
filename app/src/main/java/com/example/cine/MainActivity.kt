package com.example.cine

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.cine.ui.MovieViewModel
import com.example.cine.ui.screens.*
import com.example.cine.ui.theme.CineTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CineTheme { RootScaffold() }
        }
    }
}

private data class Tab(val route: String, val label: String, val icon: ImageVector)

@Composable
fun RootScaffold() {
    val nav = rememberNavController()
    val vm: MovieViewModel = viewModel()

    val tabs = listOf(
        Tab("home", "Trang chủ", Icons.Default.Home),
        Tab("explore", "Khám phá", Icons.Default.Search),
        Tab("favorites", "Yêu thích", Icons.Default.Favorite),
        Tab("profile", "Cá nhân", Icons.Default.Person)
    )

    Scaffold(
        bottomBar = {
            val backStack by nav.currentBackStackEntryAsState()
            val current = backStack?.destination
            val hide = current?.route?.startsWith("detail") == true ||
                    current?.route == "search"
            if (!hide) {
                NavigationBar(containerColor = MaterialTheme.colorScheme.surface) {
                    tabs.forEach { tab ->
                        val selected = current?.hierarchy?.any { it.route == tab.route } == true
                        NavigationBarItem(
                            selected = selected,
                            onClick = {
                                nav.navigate(tab.route) {
                                    popUpTo(nav.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = { Icon(tab.icon, tab.label) },
                            label = { Text(tab.label) }
                        )
                    }
                }
            }
        }
    ) { pad ->
        NavHost(nav, startDestination = "home", modifier = Modifier.padding(pad)) {
            composable("home") { HomeScreen(vm, nav) }
            composable("explore") { ExploreScreen(vm, nav) }
            composable("favorites") { FavoritesScreen(nav) }
            composable("profile") { ProfileScreen(nav) }
            composable("search") { SearchScreen(vm, nav) }
            composable("detail/{slug}") { back ->
                DetailScreen(vm, back.arguments?.getString("slug") ?: "")
            }
        }
    }
}
