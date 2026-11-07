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
import com.funpixelnest.slots.ui.common.RewardedVideoOverlay
import com.funpixelnest.slots.data.Overlay
import com.funpixelnest.slots.ui.flow.DisclaimerScreen
import com.funpixelnest.slots.ui.flow.PiggyBankDialog
import com.funpixelnest.slots.ui.flow.StoreSheet
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.funpixelnest.slots.ui.game.GameScreen
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import com.funpixelnest.slots.ui.common.AdBannerSlot
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.ui.graphics.Color
import com.funpixelnest.slots.ui.common.SafeArea
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
            Box(Modifier.fillMaxSize().background(Color(0xFF16052C))) {
            // everything stays inside the safe area (notch, status and navigation bars)
            Column(Modifier.fillMaxSize().windowInsetsPadding(SafeArea)) {
            val nav = rememberNavController()
            // screens use everything above the banner safe area
            Box(Modifier.weight(1f)) {
            var accepted by remember { mutableStateOf(graph.player.disclaimerAccepted) }
            if (!accepted) {
                DisclaimerScreen { graph.player.disclaimerAccepted = true; accepted = true }
            } else {
            NavHost(nav, startDestination = "lobby", modifier = Modifier.fillMaxSize()) {
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
            // store / piggy bank open above whichever screen asked for them
            when (graph.overlay) {
                Overlay.Store -> StoreSheet(graph) { graph.overlay = null }
                Overlay.Piggy -> PiggyBankDialog(graph) { graph.overlay = null }
                null -> Unit
            }
            }
            // safe area for a bottom banner ad on every screen
            AdBannerSlot()
            }
            // rewarded videos (machine unlocks, free coins) play above every screen
            RewardedVideoOverlay(graph.ads)
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
