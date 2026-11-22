# FunPixel Slots (native Android)

Kotlin + Jetpack Compose port of the Unity project `FunPixelNest/slots_android_New`.
Package `com.funpixelnest.slots`. Work branch: `claude/practical-fermat-xk00ny`.
The owner writes in English and Telugu (Telugu in Latin script); reply in English.

## Layout
- `engine/` pure Kotlin slot math, economy, features, quests (unit tests run on the JVM).
- `app/` Compose UI. Key files: `ui/skin/Skin.kt` (shared look), `ui/game/` (GameScreen, Cabinet, ReelsView,
  JackpotBar, GamePopups, BonusGames, GameViewModel), `ui/lobby/`, `ui/flow/FlowScreens.kt` (popups, store,
  quests, reward dialog), `data/AppGraph.kt` (player store, singletons), `ads/` (AdMob, Firebase, interstitial rules).
- `themes/theme_<id>/src/main/assets/themes/<id>/` 23 machines; `theme.json` per theme.
- `tools/` generators: `symbols/` (new symbol art), `economy/model.py`, `store/` (Play icons + screenshots),
  `prototype/screens/` (HTML design mockups), `covers/`, `unity_import/`.
- `store_assets/` Play icons (512) and screenshots (phone 1080x1920, tablet 1600x2560).

## Build and CI
- No Android SDK in the cloud container (Google downloads are blocked); APKs are built by GitHub Actions
  (`.github/workflows/android.yml`) and published to release `test-apk`:
  https://github.com/FunPixelNest/slots_scg_native/releases/download/test-apk/FunPixelSlots-test.apk
- Commit message tags: `[apk-only]` = skip engine tests (~35 min) and screenshots, just build the APK (~4 min);
  `[skip ci]` = no build. The owner often says "don't build" — then use `[skip ci]`.
- Engine tests locally (~25 min, run in background): a settings file that includes only `:engine` with the
  version catalog, then `./gradlew -p <dir> :engine:test`.

## Decisions already made (do not undo without asking)
- **Design:** "arcade night" look based on the owner's BingoFlash project: deep-space navy, glowing blue rims, glossy
  pills, Lilita One gold lettering. Gameplay: compact coins on the right, GRAND light-bulb sign with counter digits +
  MAJOR/MINOR/MINI plaques, symbols straight on the reels (no white tiles), bottom deck = bet + MAX BET left,
  rectangular SPIN right (hold = auto, no AUTO button), small purple "i" paytable by the title. Pink
  "WATCH VIDEO & GET" x2 button with gold x2 burst. Approved mockups: tools/prototype/screens (published artifact
  https://claude.ai/artifact/ECeBTY5quW9cTzwT8B8h7m).
- **Symbols:** original art for all 23 themes in `symbols_v2/`; originals kept, `theme.original.json` backups.
  Revert: `python3 tools/symbols/apply.py --revert`. New symbols have no spin blur or win animation yet.
- **Economy** (Economy.kt; model tools/economy/model.py; test keeps burn/give in 0.4-0.6): burned coins = half
  of free coins given at the minimum bet. Bets 10K..100M, 5-step window, minimum bet rises with level
  (L1 10K, L5 20K, L10 50K, L15 100K, L20 200K, L30 500K, L40 1M, L50 2M, L70 5M). All free rewards are in
  "units" of the minimum bet. Start 1M + welcome 500K. Levels 1-4 fast (XP 3/5/8/12), then 9 x level XP.
  Refill free once per 4 h, else video only. Hold & Spin pay scale 0.86 (RTP ~93%).
- **Ads:** AdMob + mediation adapters (AppLovin, Unity, Mintegral, Liftoff/Vungle); Google TEST ids until real ids
  are set as gradle properties `admob.appId`, `admob.banner`, `admob.interstitial`, `admob.rewarded`.
  3 units: banner (bottom), interstitial, rewarded. Rewarded falls back to the in-app placeholder video.
- **Interstitials** (Remote Config keys, defaults): `interstitial_every_spins`=10 (shown before the spin),
  `interstitial_every_home`=2, `interstitial_every_level_up_close`=2, `interstitial_every_big_win_close`=2,
  `interstitial_every_free_spins_win_close`=2; 0 disables. Free spins don't count.
- **Firebase** (Analytics, Crashlytics, Remote Config) is applied only when `app/google-services.json` exists.
- **App icon:** "777 SLOTS" (store_assets/icons/icon_777.png), adaptive + legacy mipmaps.

## Pending for release
Code (Claude): Google Play Billing (store currently uses `PlaceholderBilling` that grants coins for free!),
UMP consent for ads (GDPR), privacy policy link in Settings, signed release AAB (R8 rules for ads/Firebase untested),
check AAB size (~155 MB debug APK), targetSdk to Play's current requirement, full CI + device test,
Play feature graphic 1024x500, privacy policy page + app-ads.txt (website repo FunPixelNest/funpixelnest-website).
Owner: google-services.json, AdMob app id + 3 unit ids + mediation setup, upload keystore, final app name,
company name/address/support email/website, confirm in-app products (coins_1..coins_6 $0.99-$39.99,
welcome_offer $4.99, piggy_bank $2.99), Play account type (closed test 12 testers x 14 days for new personal
accounts), release countries, Play Console forms (content rating: simulated gambling 18+, data safety, ads).
