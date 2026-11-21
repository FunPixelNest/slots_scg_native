package com.funpixelnest.slots.ads

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.funpixelnest.slots.BuildConfig
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView

/** AdMob 320x50 banner for the bottom safe area. */
@Composable
fun AdMobBanner(modifier: Modifier = Modifier) {
    val ctx = LocalContext.current
    val view = remember {
        AdView(ctx).apply {
            setAdSize(AdSize.BANNER)
            adUnitId = BuildConfig.AD_BANNER
            loadAd(AdRequest.Builder().build())
        }
    }
    DisposableEffect(view) { onDispose { view.destroy() } }
    AndroidView(factory = { view }, modifier = modifier.fillMaxWidth())
}
