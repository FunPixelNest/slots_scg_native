package com.funpixelnest.slots.ui.common

import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.funpixelnest.slots.data.PlaceholderRewardedAds
import com.funpixelnest.slots.ui.skin.IconRoundButton
import com.funpixelnest.slots.ui.skin.Skin
import com.funpixelnest.slots.ui.skin.SkinButton
import com.funpixelnest.slots.ui.skin.SkinIcon
import com.funpixelnest.slots.ui.skin.SkinTone
import com.funpixelnest.slots.ui.skin.Sunburst
import kotlinx.coroutines.delay

/**
 * Stand-in for a rewarded video ad: a short countdown, then "Claim reward".
 * Closing early gives no reward, like a real rewarded ad.
 */
@Composable
fun RewardedVideoOverlay(ads: PlaceholderRewardedAds) {
    val req = ads.current ?: return
    var left by remember(req) { mutableIntStateOf(PlaceholderRewardedAds.SECONDS) }
    LaunchedEffect(req) {
        while (left > 0) { delay(1000); left-- }
    }
    Box(
        Modifier.fillMaxSize().background(Color.Black)
            .clickable(remember { MutableInteractionSource() }, null) { }
            .testTag("rewarded-video"),
        contentAlignment = Alignment.Center,
    ) {
        Sunburst(Modifier.fillMaxSize(), color = Skin.Amethyst)
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Text("REWARDED VIDEO", style = outlinedText(14.sp, Skin.TextSoft).copy(letterSpacing = 3.sp))
            CoinIcon(Modifier.size(96.dp))
            Text(
                if (left > 0) "Your reward in $left…" else "Thanks for watching!",
                style = outlinedText(22.sp, Skin.GoldLight),
            )
            Text("(placeholder until an ad network is connected)", style = outlinedText(11.sp, Color(0xFF9E9E9E)))
            if (left == 0) {
                SkinButton("CLAIM REWARD", Modifier.width(240.dp).height(56.dp), tone = SkinTone.Green, textSize = 20.sp) {
                    ads.finish(rewarded = true)
                }
            }
        }
        IconRoundButton(
            SkinIcon.Close, Modifier.align(Alignment.TopEnd).windowInsetsPadding(SafeArea).padding(14.dp),
            size = 36.dp, tone = SkinTone.Red,
        ) { ads.finish(rewarded = false) }
    }
}
