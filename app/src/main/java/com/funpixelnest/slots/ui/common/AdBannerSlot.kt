package com.funpixelnest.slots.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/** Height reserved for the bottom banner ad (standard phone banner: 320x50 dp). */
val BANNER_HEIGHT: Dp = 50.dp

/** Empty strip between the game and the banner. */
val BANNER_GAP: Dp = 6.dp

/**
 * Safe area at the bottom of every screen (lobby and machines) for a banner ad.
 * The game never draws under it, and it sits above the system navigation bar.
 *
 * To show a real ad, replace [content] with the ad network's banner view, e.g. for AdMob:
 *   AndroidView(factory = { ctx -> AdView(ctx).apply { setAdSize(AdSize.BANNER); adUnitId = "..."; loadAd(AdRequest.Builder().build()) } })
 */
@Composable
fun AdBannerSlot(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit = { BannerPlaceholder() },
) {
    // the screen above keeps BANNER_GAP clear so the footer never touches the ad
    Column(modifier.fillMaxWidth().background(Color.Black).padding(top = BANNER_GAP)) {
        Box(
            Modifier.fillMaxWidth().height(BANNER_HEIGHT).testTag("ad-banner"),
            contentAlignment = Alignment.Center,
        ) { content() }
    }
}

@Composable
private fun BannerPlaceholder() {
    Text("AD BANNER 320×50", style = outlinedText(11.sp, Color(0xFF6D6D6D)))
}
