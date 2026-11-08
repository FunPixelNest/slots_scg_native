package com.funpixelnest.slots.ui.lobby

import com.funpixelnest.slots.ui.flow.QuestsDialog
import androidx.compose.foundation.layout.offset
import androidx.compose.animation.core.tween
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.funpixelnest.slots.audio.Sfx
import com.funpixelnest.slots.data.AppGraph
import com.funpixelnest.slots.engine.Economy
import com.funpixelnest.slots.engine.Feature
import com.funpixelnest.slots.engine.Features
import com.funpixelnest.slots.engine.JackpotTier
import com.funpixelnest.slots.engine.Rewards
import com.funpixelnest.slots.engine.ThemeIndexEntry
import com.funpixelnest.slots.engine.Unlock
import com.funpixelnest.slots.engine.Unlocks
import com.funpixelnest.slots.ui.common.AssetImage
import com.funpixelnest.slots.ui.common.CoinIcon
import com.funpixelnest.slots.ui.common.CoinShower
import com.funpixelnest.slots.ui.common.formatCoins
import com.funpixelnest.slots.ui.common.formatCompact
import com.funpixelnest.slots.ui.common.outlinedText
import com.funpixelnest.slots.ui.common.rememberImage
import com.funpixelnest.slots.ui.flow.DailyWheelDialog
import com.funpixelnest.slots.ui.flow.Modal
import com.funpixelnest.slots.ui.flow.PiggyIcon
import com.funpixelnest.slots.ui.flow.RewardClaimDialog
import com.funpixelnest.slots.ui.game.PopupFrame
import com.funpixelnest.slots.ui.game.SettingsPopup
import com.funpixelnest.slots.ui.skin.CoinCapsule
import com.funpixelnest.slots.ui.skin.GoldFrame
import com.funpixelnest.slots.ui.skin.IconRoundButton
import com.funpixelnest.slots.ui.skin.LevelMedallion
import com.funpixelnest.slots.ui.skin.Skin
import com.funpixelnest.slots.ui.skin.SkinButton
import com.funpixelnest.slots.ui.skin.SkinDialog
import com.funpixelnest.slots.ui.skin.SkinIcon
import com.funpixelnest.slots.ui.skin.SkinTone
import com.funpixelnest.slots.ui.skin.VelvetBackdrop
import com.funpixelnest.slots.ui.skin.drawPadlock
import com.funpixelnest.slots.ui.skin.drawSkinIcon
import kotlinx.coroutines.delay

private fun plain(size: Int, color: Color = Color.White) = outlinedText(size.sp, color).copy(shadow = null)

/** First-run / once-a-day popups shown in order on entering the lobby. */
private enum class Startup { Welcome, Daily, Wheel }

/**
 * Lobby: profile + balance, featured machine, quick bonuses, filter chips, two-column grid of
 * machines (vertical scroll), floating tab bar.
 */
@Composable
fun LobbyScreen(
    graph: AppGraph,
    onOpenTheme: (String) -> Unit,
    /** Screenshot hooks. */
    startWithDailyBonus: Boolean = false,
    startWithUnlocks: Boolean = false,
    startWithLocked: String? = null,
    skipStartup: Boolean = false,
    /** Screenshot hook: "tasks" or "missions". */
    startWithQuests: String? = null,
) {
    val player = graph.player
    val coins by player.coinsFlow.collectAsState()
    val level by player.levelFlow.collectAsState()
    val progress by player.progressFlow.collectAsState()
    var showSettings by remember { mutableStateOf(false) }
    var showUnlocks by remember { mutableStateOf(startWithUnlocks) }
    var hourlyClaim by remember { mutableStateOf(false) }
    var quests by remember { mutableStateOf(startWithQuests) }
    val questTick by player.questsFlow.collectAsState()
    var locked by remember { mutableStateOf(graph.themes.firstOrNull { it.id == startWithLocked }) }
    var now by remember { mutableLongStateOf(System.currentTimeMillis()) }
    var shower by remember { mutableIntStateOf(0) }
    var refresh by remember { mutableIntStateOf(0) }
    var filter by remember { mutableStateOf<String?>(null) }

    // startup queue: welcome bonus -> daily bonus -> daily wheel
    var startup by remember {
        mutableStateOf(
            when {
                startWithDailyBonus -> listOf(Startup.Daily)
                skipStartup || startWithUnlocks || startWithLocked != null || startWithQuests != null -> emptyList()
                else -> buildList {
                    if (!player.welcomeClaimed) add(Startup.Welcome)
                    if (player.nextDailyDay(now) != null) add(Startup.Daily)
                    if (player.wheelReadyIn(now) == 0L) add(Startup.Wheel)
                }
            }
        )
    }
    fun nextStartup() { startup = startup.drop(1); shower++ }

    LaunchedEffect(Unit) {
        graph.sound.startMusic()
        while (true) { delay(1000); now = System.currentTimeMillis() }
    }

    val filters = listOf<Pair<String, (ThemeIndexEntry) -> Boolean>>(
        "Jackpots" to { e -> Features.forTheme(e.id)?.hasJackpots == true },
        "Free Spins" to { e -> Features.forTheme(e.id) == Feature.FREE_SPINS_MULTIPLIER },
        "Cascade" to { e -> Features.forTheme(e.id) == Feature.CASCADE },
        "Collect" to { e -> Features.forTheme(e.id).let { it == Feature.COLLECT_METER || it == Feature.COIN_COLLECTOR } },
        "Bonus Games" to { e -> Features.forTheme(e.id)?.isBonusGame == true },
        "Unlocked" to { e -> player.isOpen(e) },
    )
    val shown = remember(filter, refresh, level) {
        val f = filters.firstOrNull { it.first == filter }?.second
        if (f == null) graph.themes else graph.themes.filter(f)
    }
    val unlocked = remember(refresh, level) { graph.themes.count { player.isOpen(it) } }

    Box(Modifier.fillMaxSize().background(Skin.VelvetDeep).testTag("lobby")) {
        VelvetBackdrop(Modifier.fillMaxSize())

        LazyVerticalGrid(
            GridCells.Fixed(2),
            Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 14.dp, end = 14.dp, top = 8.dp, bottom = 96.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                ProfileBar(level, progress, coins, onPlus = { graph.sound.play(Sfx.Button); graph.openStore() }) {
                    graph.sound.play(Sfx.Button); showSettings = true
                }
            }
            item(span = { GridItemSpan(maxLineSpan) }) {
                val featured = remember { FEATURED.mapNotNull { id -> graph.themes.firstOrNull { it.id == id } } }
                FeaturedCarousel(graph, featured, isOpen = { player.isOpen(it) }) { hero ->
                    if (player.isOpen(hero)) onOpenTheme(hero.id) else locked = hero
                }
            }
            item(span = { GridItemSpan(maxLineSpan) }) {
                QuickRow(graph, now, onDaily = { startup = listOf(Startup.Daily) }, onHourly = { hourlyClaim = true }, onPiggy = { graph.openPiggy() })
            }
            item(span = { GridItemSpan(maxLineSpan) }) {
                Row(Modifier.fillMaxWidth().padding(top = 6.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text("All Games", style = plain(18))
                    Spacer(Modifier.weight(1f))
                    Text("$unlocked / ${graph.themes.size} UNLOCKED", style = plain(10, Skin.TextSoft).copy(letterSpacing = 1.5.sp))
                }
            }
            item(span = { GridItemSpan(maxLineSpan) }) {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    item { FilterChip("All", filter == null) { filter = null } }
                    items(filters.map { it.first }) { name -> FilterChip(name, filter == name) { filter = if (filter == name) null else name } }
                }
            }
            items(shown, key = { it.id }) { entry ->
                val open = remember(refresh, level) { player.isOpen(entry) }
                MachineCard(graph, entry, open) {
                    if (open) { graph.sound.play(Sfx.Choose); onOpenTheme(entry.id) } else { graph.sound.play(Sfx.Button); locked = entry }
                }
            }
        }
        // fade + floating tab bar
        Box(Modifier.align(Alignment.BottomCenter).fillMaxWidth().height(110.dp).background(Brush.verticalGradient(listOf(Color.Transparent, Skin.VelvetDeep))))
        val taskAlert = remember(questTick, now / 60_000) { player.claimableTasks() > 0 }
        val missionAlert = remember(questTick, now / 60_000, level) { player.claimableMissions() > 0 }
        TabBar(
            Modifier.align(Alignment.BottomCenter), taskAlert, missionAlert,
            onStore = { graph.sound.play(Sfx.Button); graph.openStore() },
            onTasks = { graph.sound.play(Sfx.Button); quests = "tasks" },
            onMissions = { graph.sound.play(Sfx.Button); quests = "missions" },
            onPiggy = { graph.sound.play(Sfx.Button); graph.openPiggy() },
        )

        if (showSettings) SettingsPopup(graph, graph.commonImages, null) { showSettings = false }
        CoinShower(shower, 60, Modifier.fillMaxSize())
        if (showUnlocks) UnlocksPopup(graph, level) { showUnlocks = false }
        quests?.let { q -> QuestsDialog(graph, daily = q == "tasks") { quests = null; shower++ } }
        if (hourlyClaim) {
            RewardClaimDialog(graph, "Hourly Bonus", "Free coins every hour", Economy.hourlyBonus(level)) {
                player.lastHourlyAt = System.currentTimeMillis(); hourlyClaim = false; shower++
            }
        }
        locked?.let { entry ->
            LockedMachinePopup(graph, entry, level, onWatch = {
                locked = null
                graph.ads.show("unlock_${entry.id}") { player.addVideoWatched(entry.id); graph.sound.play(Sfx.Bonus); refresh++; shower++ }
            }) { locked = null }
        }
        when (startup.firstOrNull()) {
            Startup.Welcome -> RewardClaimDialog(graph, "Welcome!", "Here's a gift to get you started", Rewards.WELCOME_BONUS) {
                player.welcomeClaimed = true; nextStartup()
            }
            Startup.Daily -> DailyBonusPopup(graph, now, onDone = { nextStartup() })
            Startup.Wheel -> if (player.wheelReadyIn(now) == 0L) DailyWheelDialog(graph) { nextStartup() } else LaunchedEffect(Unit) { nextStartup() }
            null -> Unit
        }
    }
}

@Composable
fun ProfileBar(level: Int, progress: Float, coins: Long, onPlus: () -> Unit, onSettings: () -> Unit) {
    Row(Modifier.fillMaxWidth().padding(vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
        LevelMedallion(level, progress, size = 48.dp)
        Column(Modifier.weight(1f).padding(start = 10.dp)) {
            Text("Lucky Player", style = plain(14).copy(textAlign = TextAlign.Start))
            Text("LEVEL $level · ${progress.toInt()}%", style = plain(10, Skin.TextSoft).copy(letterSpacing = 1.sp, textAlign = TextAlign.Start))
            Box(Modifier.padding(top = 4.dp).width(110.dp).height(5.dp).background(Color(0x1FFFFFFF), RoundedCornerShape(50))) {
                Box(Modifier.fillMaxHeight().fillMaxWidth((progress / 100f).coerceIn(0.03f, 1f)).background(Brush.horizontalGradient(listOf(Color(0xFFFF3DB4), Color(0xFFB45CFF))), RoundedCornerShape(50)))
            }
        }
        CoinCapsule(coins, onPlus = onPlus)
        Spacer(Modifier.width(8.dp))
        IconRoundButton(SkinIcon.Gear, size = 38.dp, onClick = onSettings)
    }
}

/** Machines shown in the auto-scrolling banner at the top of the lobby. */
private val FEATURED = listOf("china", "pirates", "egypt", "greek", "gold_mine") // all jackpot machines (the banner shows the GRAND)
private const val FEATURED_INTERVAL_MS = 4_000L

/** Five featured machines that slide by themselves every [FEATURED_INTERVAL_MS] (and can be swiped). */
@Composable
private fun FeaturedCarousel(graph: AppGraph, featured: List<ThemeIndexEntry>, isOpen: (ThemeIndexEntry) -> Boolean, onPlay: (ThemeIndexEntry) -> Unit) {
    if (featured.isEmpty()) return
    val start = (Int.MAX_VALUE / 2).let { it - it % featured.size }
    val pager = rememberPagerState(initialPage = start) { Int.MAX_VALUE }
    LaunchedEffect(pager.settledPage) {
        delay(FEATURED_INTERVAL_MS)
        pager.animateScrollToPage(pager.settledPage + 1, animationSpec = tween(600))
    }
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        HorizontalPager(pager, Modifier.fillMaxWidth(), pageSpacing = 12.dp) { page ->
            val e = featured[page % featured.size]
            HeroBanner(graph, e, isOpen(e)) { onPlay(e) }
        }
        Row(Modifier.padding(top = 8.dp), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            val cur = pager.currentPage % featured.size
            featured.indices.forEach { i ->
                Box(
                    Modifier.height(6.dp).width(if (i == cur) 18.dp else 6.dp)
                        .background(if (i == cur) Skin.Gold else Color(0x40FFFFFF), RoundedCornerShape(50)),
                )
            }
        }
    }
}

@Composable
private fun HeroBanner(graph: AppGraph, entry: ThemeIndexEntry, open: Boolean, onPlay: () -> Unit) {
    val grand = Economy.TOTAL_BETS[0].toLong() * JackpotTier.GRAND.betMultiple + graph.player.jackpotPool(entry.id, JackpotTier.GRAND)
    val assets = remember(entry.id) { graph.theme(entry.id) }
    val bgKey = assets?.config?.let { it.backgroundPortrait ?: it.background }
    Box(
        Modifier.fillMaxWidth().height(196.dp).shadow(20.dp, RoundedCornerShape(24.dp)).clip(RoundedCornerShape(24.dp))
            .clickable(onClick = onPlay),
    ) {
        if (assets != null) AssetImage(assets, bgKey, Modifier.fillMaxSize(), ContentScale.Crop)
        Box(Modifier.fillMaxSize().background(Brush.horizontalGradient(listOf(Color(0xEB16052C), Color(0x8C16052C), Color.Transparent))))
        Column(Modifier.fillMaxHeight().padding(18.dp), verticalArrangement = Arrangement.SpaceBetween) {
            Features.forTheme(entry.id)?.let { f ->
                Text("● ${f.title.uppercase()}", style = plain(11, Color(0xFFFF8DB8)), modifier = Modifier
                    .background(Color(0x2EFF3D8B), RoundedCornerShape(50)).border(1.dp, Color(0x66FF3D8B), RoundedCornerShape(50))
                    .padding(horizontal = 10.dp, vertical = 4.dp))
            }
            Column {
                Text(entry.name, style = plain(30).copy(textAlign = TextAlign.Start))
                Text("GRAND JACKPOT", style = plain(10, Skin.TextSoft).copy(letterSpacing = 2.sp, textAlign = TextAlign.Start))
                Text(formatCoins(grand), style = plain(26, Skin.Gold).copy(textAlign = TextAlign.Start))
            }
            SkinButton(if (open) "Play now" else "Unlock", Modifier.width(122.dp).height(40.dp), tone = SkinTone.Gold, onClick = onPlay)
        }
    }
}

@Composable
private fun QuickRow(graph: AppGraph, now: Long, onDaily: () -> Unit, onHourly: () -> Unit, onPiggy: () -> Unit) {
    val p = graph.player
    val hourlyLeft = p.hourlyReadyIn(now)
    val piggy by p.piggyFlow.collectAsState()
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        QuickTile(SkinIcon.Gift, Skin.Ruby, "Daily", p.nextDailyDay(now) != null, Modifier.weight(1f), onDaily)
        QuickTile(SkinIcon.Clock, Skin.Sapphire, if (hourlyLeft == 0L) "Collect" else countdown(hourlyLeft), hourlyLeft == 0L, Modifier.weight(1f)) {
            if (hourlyLeft == 0L) onHourly()
        }
        QuickTile(SkinIcon.Video, Skin.Gold, "+${formatCompact(p.videoCoinsAmount())}", p.videoCoinsReadyIn(now) == 0L, Modifier.weight(1f)) {
            graph.ads.show("free_coins") { if (p.claimVideoCoins(System.currentTimeMillis()) > 0) graph.sound.play(Sfx.WinCoins) }
        }
        Column(
            Modifier.weight(1f).height(80.dp).background(Color(0x0FFFFFFF), RoundedCornerShape(18.dp)).border(1.dp, Skin.Hairline, RoundedCornerShape(18.dp))
                .clickable(remember { MutableInteractionSource() }, null, onClick = onPiggy),
            horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center,
        ) {
            PiggyIcon(Modifier.size(36.dp))
            Text(formatCompact(piggy), style = plain(10))
        }
    }
}

@Composable
private fun QuickTile(icon: SkinIcon, accent: Color, label: String, alert: Boolean, modifier: Modifier, onClick: () -> Unit) {
    Box(
        modifier.height(80.dp).shadow(10.dp, RoundedCornerShape(18.dp), spotColor = Skin.Ruby)
            .background(Brush.verticalGradient(listOf(Color(0xFF5A1C9A), Color(0xFF2E0A5A))), RoundedCornerShape(18.dp))
            .border(1.5.dp, Skin.goldBorder, RoundedCornerShape(18.dp))
            .clickable(remember { MutableInteractionSource() }, null, onClick = onClick),
    ) {
        Column(Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
            Box(Modifier.size(36.dp).background(accent.copy(alpha = 0.28f), RoundedCornerShape(12.dp)), contentAlignment = Alignment.Center) {
                Canvas(Modifier.size(18.dp)) { drawSkinIcon(icon, accent) }
            }
            Spacer(Modifier.height(6.dp))
            Text(label, style = plain(10))
        }
        if (alert) Box(Modifier.align(Alignment.TopEnd).padding(8.dp).size(8.dp).shadow(6.dp, CircleShape, spotColor = Skin.Ruby).background(Skin.Ruby, CircleShape))
    }
}

@Composable
private fun FilterChip(text: String, on: Boolean, onClick: () -> Unit) {
    Text(
        text, style = plain(11, if (on) Color(0xFF3A1800) else Color.White),
        modifier = Modifier
            .background(if (on) Skin.goldFill else Brush.verticalGradient(listOf(Color(0xFF4A1580), Color(0xFF2A0B4A))), RoundedCornerShape(50))
            .border(1.dp, if (on) Skin.goldBorder else Brush.verticalGradient(listOf(Skin.Hairline, Skin.Hairline)), RoundedCornerShape(50))
            .clickable(onClick = onClick).padding(horizontal = 12.dp, vertical = 6.dp),
    )
}

/** Machine card: art, name, feature, jackpot chip; locked ones greyed with the unlock rule. */
@Composable
private fun MachineCard(graph: AppGraph, entry: ThemeIndexEntry, open: Boolean, onClick: () -> Unit) {
    val rule = Unlocks.rule(entry.order)
    val grey = remember { ColorFilter.colorMatrix(ColorMatrix().apply { setToSaturation(0.1f) }) }
    Box(
        // painted icon (600x504) inside a glowing gold frame
        Modifier.fillMaxWidth().aspectRatio(600f / 504f)
            .shadow(14.dp, RoundedCornerShape(20.dp), ambientColor = Skin.Gold, spotColor = Skin.Gold)
            .border(3.dp, Brush.verticalGradient(listOf(Color(0xFFFFF3B0), Skin.Gold, Color(0xFFB8740F), Skin.Gold)), RoundedCornerShape(20.dp))
            .padding(3.dp).clip(RoundedCornerShape(17.dp))
            .clickable(onClick = onClick),
    ) {
        val img = rememberImage(graph.commonImages, entry.tile)
        if (img != null) {
            androidx.compose.foundation.Image(
                img, null, Modifier.fillMaxSize().then(if (open) Modifier else Modifier.blur(1.5.dp)), contentScale = ContentScale.Crop,
                colorFilter = if (open) null else grey, alpha = if (open) 1f else 0.55f,
            )
        }
        if (!open) {
            Column(Modifier.align(Alignment.Center), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Box(Modifier.size(44.dp).background(Color(0x1FFFFFFF), CircleShape).border(1.dp, Skin.Hairline, CircleShape), contentAlignment = Alignment.Center) {
                    Canvas(Modifier.size(20.dp)) { drawPadlock(Offset.Zero, size.width, Color.White) }
                }
                when (rule) {
                    is Unlock.WatchVideo -> Text("▶ Watch to unlock", style = plain(11, Color(0xFF03230F)), modifier = Modifier.background(Skin.Emerald, RoundedCornerShape(50)).padding(horizontal = 10.dp, vertical = 4.dp))
                    is Unlock.AtLevel -> Text("Level ${rule.level}", style = plain(11), modifier = Modifier.background(Color(0x26FFFFFF), RoundedCornerShape(50)).border(1.dp, Skin.Hairline, RoundedCornerShape(50)).padding(horizontal = 10.dp, vertical = 4.dp))
                    Unlock.Free -> Unit
                }
            }
        }
    }
}

@Composable
private fun TabBar(
    modifier: Modifier, taskAlert: Boolean, missionAlert: Boolean,
    onStore: () -> Unit, onTasks: () -> Unit, onMissions: () -> Unit, onPiggy: () -> Unit,
) {
    Box(modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 10.dp), contentAlignment = Alignment.BottomCenter) {
        Row(
            Modifier.fillMaxWidth().height(64.dp)
                .shadow(24.dp, RoundedCornerShape(22.dp))
                .background(Color(0xE62A0B4A), RoundedCornerShape(22.dp)).border(1.dp, Skin.Hairline, RoundedCornerShape(22.dp)),
            horizontalArrangement = Arrangement.SpaceAround, verticalAlignment = Alignment.CenterVertically,
        ) {
            TabItem(SkinIcon.Cart, "Store", false, onClick = onStore)
            TabItem(SkinIcon.Tasks, "Tasks", taskAlert, onClick = onTasks)
            Spacer(Modifier.width(72.dp)) // room for the raised Lobby button
            TabItem(SkinIcon.Target, "Missions", missionAlert, onClick = onMissions)
            TabItem(SkinIcon.Piggy, "Piggy", false, onClick = onPiggy)
        }
        // Lobby: raised gold button in the middle (current screen)
        Column(Modifier.padding(bottom = 6.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                Modifier.size(64.dp).shadow(16.dp, CircleShape, spotColor = Skin.Gold, ambientColor = Skin.Gold)
                    .background(Brush.verticalGradient(listOf(Color(0xFFFFE08A), Skin.Gold, Color(0xFFC77A0A))), CircleShape)
                    .padding(3.dp).background(Brush.verticalGradient(listOf(Color(0xFF2A1B4A), Color(0xFF120A26))), CircleShape),
                contentAlignment = Alignment.Center,
            ) { Canvas(Modifier.size(28.dp)) { drawSkinIcon(SkinIcon.Home, Skin.Gold) } }
            Text("Lobby", style = plain(10, Skin.Gold), modifier = Modifier.padding(top = 2.dp))
        }
    }
}

@Composable
private fun TabItem(icon: SkinIcon, label: String, alert: Boolean, onClick: () -> Unit) {
    Column(
        Modifier.width(60.dp).clickable(remember { MutableInteractionSource() }, null, onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box {
            Canvas(Modifier.size(22.dp)) { drawSkinIcon(icon, Color.White) }
            if (alert) Box(Modifier.align(Alignment.TopEnd).offset(x = 5.dp, y = (-3).dp).size(9.dp).background(Skin.Ruby, CircleShape))
        }
        Spacer(Modifier.height(4.dp))
        Text(label, style = plain(10, Skin.TextSoft))
    }
}

private fun countdown(ms: Long): String {
    val s = ms / 1000
    return if (s >= 3600) "%d:%02d:%02d".format(s / 3600, s / 60 % 60, s % 60) else "%02d:%02d".format(s / 60, s % 60)
}

/** Loading bar used on machine load screens. */
@Composable
fun LoadBar(@Suppress("UNUSED_PARAMETER") graph: AppGraph, progress: Float, modifier: Modifier) {
    Box(modifier.background(Color(0x1FFFFFFF), RoundedCornerShape(50))) {
        Box(Modifier.fillMaxHeight().fillMaxWidth(progress.coerceIn(0.03f, 1f)).background(Brush.horizontalGradient(listOf(Skin.Gold, Skin.GoldLight)), RoundedCornerShape(50)))
    }
}

/** Seven-day streak; COLLECT or COLLECT x2 (video). */
@Composable
private fun DailyBonusPopup(graph: AppGraph, now: Long, onDone: () -> Unit) {
    val player = graph.player
    val today = player.nextDailyDay(now)
    val streakDay = today ?: player.lastDailyDay
    var claimAmount by remember { mutableLongStateOf(0L) }
    if (claimAmount > 0) {
        RewardClaimDialog(graph, "Daily Bonus", "Day $streakDay reward", claimAmount, alreadyGranted = true) { onDone() }
        return
    }
    Modal(onDismiss = onDone, tag = "daily-bonus") {
        SkinDialog("Daily Bonus", Modifier.fillMaxWidth(), tone = SkinTone.Red) {
            Text("Come back every day. Day 7 pays the most!", style = plain(12, Skin.TextSoft))
            Spacer(Modifier.height(12.dp))
            for (days in listOf(1..4, 5..Economy.DAILY_DAYS)) Row(Modifier.padding(bottom = 8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                for (d in days) {
                    val claimed = (today == null && d <= streakDay) || (today != null && d < today)
                    val isToday = d == today
                    Column(
                        Modifier.weight(if (d == 7) 2f else 1f).height(96.dp)
                            .background(
                                when {
                                    isToday -> Brush.verticalGradient(listOf(Color(0x40F5C451), Color(0x14F5C451)))
                                    else -> Brush.verticalGradient(listOf(Color(0x14FFFFFF), Color(0x08FFFFFF)))
                                }, RoundedCornerShape(14.dp),
                            )
                            .border(1.dp, if (isToday) Skin.Gold else Skin.Hairline, RoundedCornerShape(14.dp)).padding(6.dp),
                        horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Text("DAY $d", style = plain(10, if (isToday) Skin.Gold else Skin.TextSoft))
                        CoinIcon(Modifier.size(if (d == 7) 36.dp else 26.dp))
                        Text(if (claimed) "✓" else formatCompact(Economy.dailyBonus(d, player.level)), style = plain(12, if (claimed) Skin.Emerald else Color.White))
                    }
                }
            }
            Spacer(Modifier.height(8.dp))
            if (today != null) {
                SkinButton("CLAIM DAY $today", Modifier.fillMaxWidth().height(52.dp), tone = SkinTone.Gold, textSize = 16.sp) {
                    claimAmount = player.claimDaily(System.currentTimeMillis())
                    graph.sound.play(Sfx.WinCoins)
                }
            } else {
                Text("Next bonus in ${countdown(player.lastDailyAt + Economy.DAY_MS - now)}", style = plain(13))
                Spacer(Modifier.height(8.dp))
                SkinButton("OK", Modifier.fillMaxWidth().height(46.dp), tone = SkinTone.Glass, onClick = onDone)
            }
        }
    }
}

/** Tapping a locked machine: shows what opens it, with a watch-video shortcut where allowed. */
@Composable
private fun LockedMachinePopup(graph: AppGraph, entry: ThemeIndexEntry, level: Int, onWatch: () -> Unit, onDismiss: () -> Unit) {
    val rule = Unlocks.rule(entry.order)
    PopupFrame(entry.name, onDismiss, tone = SkinTone.Purple, widthFraction = 0.86f) {
        Box(Modifier.width(200.dp).aspectRatio(600f / 504f)) {
            GoldFrame(Modifier.fillMaxSize()) { AssetImage(graph.commonImages, entry.tile, Modifier.fillMaxSize(), ContentScale.Crop) }
        }
        when (rule) {
            is Unlock.AtLevel -> {
                Text("Reach level ${rule.level} to play.", style = plain(16))
                Text("You are level $level. Every spin earns XP.", style = plain(12, Skin.TextSoft))
                SkinButton("OK", Modifier.fillMaxWidth().height(46.dp), tone = SkinTone.Glass, onClick = onDismiss)
            }
            is Unlock.WatchVideo -> {
                Text("Watch a short video to unlock now,\nor reach level ${rule.level}.", style = plain(15))
                SkinButton("▶  WATCH & UNLOCK", Modifier.fillMaxWidth().height(52.dp), tone = SkinTone.Green, textSize = 16.sp, onClick = onWatch)
            }
            Unlock.Free -> Unit
        }
    }
}

/** All machines with how each one unlocks. */
@Composable
private fun UnlocksPopup(graph: AppGraph, level: Int, onDismiss: () -> Unit) {
    PopupFrame("Machine Unlocks", onDismiss, tone = SkinTone.Gold, widthFraction = 0.94f, heightFraction = 0.8f) {
        Text("You are level $level. Machines open as you level up.", style = plain(12, Skin.TextSoft))
        LazyVerticalGrid(GridCells.Fixed(3), Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(10.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(graph.themes) { e ->
                val open = graph.player.isOpen(e)
                val rule = Unlocks.rule(e.order)
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(Modifier.fillMaxWidth().aspectRatio(600f / 504f).clip(RoundedCornerShape(12.dp))) {
                        AssetImage(graph.commonImages, e.tile, Modifier.fillMaxSize(), ContentScale.Crop, alpha = if (open) 1f else 0.4f)
                        if (!open) Canvas(Modifier.size(24.dp).align(Alignment.Center)) { drawPadlock(Offset.Zero, size.width, Color.White) }
                    }
                    Text(
                        when {
                            open -> "✓ Open"
                            rule is Unlock.AtLevel -> "Level ${rule.level}"
                            rule is Unlock.WatchVideo -> "▶ Video / Lv ${rule.level}"
                            else -> ""
                        },
                        style = plain(10, if (open) Skin.Emerald else Skin.TextSoft), modifier = Modifier.padding(top = 4.dp),
                    )
                }
            }
        }
        SkinButton("CLOSE", Modifier.fillMaxWidth().height(46.dp), tone = SkinTone.Glass, onClick = onDismiss)
    }
}
