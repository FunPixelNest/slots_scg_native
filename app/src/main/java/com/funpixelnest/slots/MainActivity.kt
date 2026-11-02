package com.funpixelnest.slots

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.funpixelnest.slots.data.AppGraph
import com.funpixelnest.slots.ui.game.GameScreen
import com.funpixelnest.slots.ui.lobby.LobbyScreen

class MainActivity : ComponentActivity() {
    private val graph by lazy { AppGraph.get(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowInsetsControllerCompat(window, window.decorView).apply {
            hide(WindowInsetsCompat.Type.systemBars())
            systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }

        setContent {
            val nav = rememberNavController()
            NavHost(nav, startDestination = "lobby") {
                composable("lobby") {
                    LobbyScreen(
                        graph = graph,
                        onOpenTheme = { id -> nav.navigate("game/$id") },
                    )
                }
                composable("game/{id}") { entry ->
                    val id = entry.arguments?.getString("id").orEmpty()
                    GameScreen(graph, id, onExit = { nav.popBackStack("lobby", inclusive = false) })
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        graph.sound.onResume()
    }

    override fun onPause() {
        graph.sound.onPause()
        super.onPause()
    }
}
