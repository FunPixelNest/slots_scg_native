package com.funpixelnest.slots.ui.common

import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.systemBarsIgnoringVisibility
import androidx.compose.foundation.layout.union
import androidx.compose.runtime.Composable

/**
 * Safe area for every screen: status bar, navigation bar and camera cutout. Reserved even while
 * the bars are hidden (immersive mode), so the HUD and footer never slide under them when the
 * player swipes the bars in.
 */
@OptIn(ExperimentalLayoutApi::class)
val SafeArea: WindowInsets
    @Composable get() = WindowInsets.systemBarsIgnoringVisibility.union(WindowInsets.displayCutout)
