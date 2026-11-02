# slots_scg_native

Native Android (Kotlin + Jetpack Compose) version of the Unity project
[`slots_android_New`](https://github.com/FunPixelNest/slots_android_New) ("MK Holiday Slot Bundle"):
a lobby plus **23 slot machines**, using the original Unity UI art.

## Project layout

| Path | What it is |
| --- | --- |
| `engine/` | Pure Kotlin slot engine (no Android): reel strips, 243-ways evaluation, wilds, scatters, free spins, wallet/level rules. Unit-tested on the JVM. |
| `app/` | Android app: lobby, game screen, reel animation, popups, sound, saved progress. |
| `app/src/main/assets/common/` | Shared art (lobby, common UI, FX), audio (OGG) and `themes.json` (lobby index). |
| `app/src/main/res/font/` | Fonts shipped with the Unity project (Montserrat, Coiny, Lilita One, Corben). |
| `themes/theme_<id>/` | One **Play Asset Delivery** pack per slot theme: all of that theme's art (WebP), plus `theme.json`, `sprites.json` and `images.json`. |
| `tools/unity_import/` | The converter that produced everything above from the Unity project. |

## Themes

Christmas Eve, Valentine's Day, Witch's Cauldron, Modern Suits, Enchanted Forest, Neon Nights, Underwater,
Wolf Moon, Candy Land, Pirates, Tropical, Gold Mine, Happy Farm, Most Wanted, Gems, Egypt, Egypt II, China,
Greek Gods, Vikings, India, Arabian Nights, Wild West.

* **Christmas Eve, Valentine's Day, Witch** have Unity scenes, so their reel strips, paytable, wild/scatter rules
  and bets are extracted directly from the scenes (`source: "unity-scene:..."` in `theme.json`).
* The other 20 themes were art-only in Unity (no scene). They use the same math template
  (`tools/unity_import/math_template.json`) mapped onto their own symbols: 4 low symbols (cards/suits),
  4 high symbols, Wild and Scatter. *Gems* has no scatter art, so it plays without scatters.

## Re-importing art from Unity

```bash
pip install pillow   # and ffmpeg on PATH for audio
python3 tools/unity_import/import_unity.py \
    --unity ../slots_android_New/Assets/HolidaySlotAsset \
    --out .
```

* Every PNG is converted to WebP (max 2048 px, quality 82), keeping the Unity folder structure with names in
  `lower_snake_case`. PSDs are skipped. 6,367 PNGs (1.0 GB) become about 135 MB.
* Sprite-sheet frame rectangles and 9-slice borders are read from the Unity `.meta` files into `sprites.json`.
* Background, reel frame, symbols, blurred symbols, win animations, buttons, panels and popups are picked by
  naming convention. Anything picked wrongly can be fixed in `tools/unity_import/overrides.json`
  (see the `_comment` there), then re-run with `--skip-images` to only regenerate the JSON.
* `--only 9_WolfTheme` re-imports a single theme.

## Building

```bash
./gradlew :engine:test          # slot math tests + RTP simulation for every theme
./gradlew :app:assembleDebug    # debug APK, every theme bundled inside (no Play needed)
./gradlew :app:bundleRelease    # release .aab, themes delivered as Play Asset Delivery packs
```

The GitHub Actions workflow (`.github/workflows/android.yml`) runs all three and uploads the APK/AAB.

**Theme delivery:** in the release bundle each theme is a `fast-follow` asset pack, so Play downloads them right after
install. The lobby shows download progress or a "tap to download" card for a theme that is not on the device yet.
Debug builds put every theme straight into the APK.

To test the release asset-pack flow locally:

```bash
bundletool build-apks --bundle=app/build/outputs/bundle/release/app-release.aab --output=slots.apks --local-testing
```

```bash
bundletool install-apks --apks=slots.apks
```

## What was ported from Unity

| Unity (C#) | Native |
| --- | --- |
| `SlotController`, `WinController`, `LineBehavior`, `PayLine.GetWildLines` | `engine/SlotMachine.kt` |
| `SlotControls`, `SlotPlayer`, win/lose states | `engine/GameSession.kt`, `ui/game/GameViewModel.kt` |
| `SlotGroupBehavior` reel spin (wind-up, spin, bounce) | `ui/game/ReelsView.kt` |
| Win symbol animations (sprite sheets / frame sequences), line flashing | `ui/game/ReelsView.kt` |
| Big/Mega/Huge win, free spins, level-up, info/paytable, settings popups | `ui/game/GamePopups.kt` |
| Lobby, time gift, sound/music settings | `ui/lobby/LobbyScreen.kt`, `data/AppGraph.kt` |
| `SoundMaster` | `audio/SoundPlayer.kt` |
| PlayerPrefs | SharedPreferences (`PrefsPlayerStore`) |

Payout rules follow Unity: coins won = (line pays + scatter pays) × pay multipliers × line bet. The total bet is
line bet × 243. Unity also multiplied won free spins by the line bet. That is off here
(`BetConfig.freeSpinsScaleWithBet`), because it gives 100 free spins at max bet.

**Math note:** the Unity paytable gives about **69 % RTP** with an 87 % hit rate (many tiny wins), which drains coins
quickly. `./gradlew :engine:test` prints the simulated RTP per theme. Tune `math_template.json` (or a theme's
`theme.json`) and re-run the tests.

### Not ported yet

The Hold feature (`02h_Christmas_..._Hold` scene), progressive jackpots, the fortune wheel / daily spin, achievements,
ads (`AdsControl`) and in-app purchases (`MKUtils/Purchasing`). The art for these is already in each theme pack.
