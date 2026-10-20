# Xiaomi TV launcher project state

## Current identity and repository state

Display name: MiOS Launcher. Android versionName: 6.10.21. versionCode: 6. Application ID: com.gathin.tvhome. The owner supplied icon.png and banner.png; canonical branding is in assets/branding. Do not change the package ID or signing identity casually: existing installs depend on them.

On 2026-10-21 the owner requested removing all v0.1 snapshots and cleaning the project for Git. The old v0.1 label was only a backup label, not the Android version. The only current installation artifact is artifacts/MiOS-Launcher-6.10.21.apk (ignored by Git). Source is in clean-your-tv/launcher, editable assets in assets, build/recovery scripts in tools. Application code, tests, licenses, Gradle wrapper and relevant upstream device/recovery support are retained.

Private local state: .android/debug.keystore and tv-backup recovery records must remain excluded from Git. Installed JDK/SDK/ADB tools stay local and ignored so this workspace can still build and connect. Caches/build outputs can be regenerated. Canonical branding belongs in assets/branding; read its README.

## Current functionality

Native Android TV launcher for MiTV-MOOR2, Android 11, with SF Pro Display, editable brand artwork, card-anchored app menus, compact rows, stable focus navigation, sliding Home/Apps transitions, 10 background themes, dimming and transition controls, and configurable clock/screensaver typography. Latest validation: 135 tests passed, zero lint errors. Detailed implementation history: clean-your-tv/docs/UI-IMPLEMENTATION.md.

## Working paths

- Android project: clean-your-tv/launcher
- Editable artwork and mappings: assets/app-icons; edit the canonical SVGs directly in assets/app-icons
- Background recipe: assets/backgrounds/aurora.json
- Build: powershell.exe -NoProfile -ExecutionPolicy Bypass -File tools/Build-Moor2Launcher.ps1
- Current working APK: artifacts/MiOS-Launcher-6.10.21.apk

Retain the Gradle wrapper and original signing key. Follow TRANSFER-GUIDE.md on another PC; the private signing key is transferred separately, never via Git.

## TV constraints

The TV keeps its original firmware; do not root, unlock or flash it as part of routine launcher work. Preserve HDMI/HDR/CEC, audio, network, setup/recovery, updates and Play Store services. The user's keep-list apps are NoTubeTV, Downloader, TiviMate, Play Store, Stremio, Netflix, Deezer, Spotify, Speedtest, Smarters IPTV, Kick and Prime Video. Cleanup recovery records remain in tv-backup/unused-app-cleanup and tools/Clean-Moor2UnusedApps.ps1. Existing Home override and Xiaomi cleanup approvals are documented in the project history. Avoid force-stopping the launcher because it can stale the Home accessibility binding on this TV.

## Moving to another PC

Read TRANSFER-GUIDE.md. The source repository includes this guide and tools/Configure-LocalBuild.ps1. Configure JDK 17 and Android SDK 34 / Build Tools 34.0.0 for the new PC; retain .android/debug.keystore. An MD alone does not contain the project or tools.

The owner also requests target-TV cleanup by the receiving agent after providing that TV's keep-list. Follow the cleanup workflow in TRANSFER-GUIDE.md: inventory the actual model, remove optional apps outside the supplied list, disable/remove verified Xiaomi promotions and analytics, clean disposable caches/temp files, preserve necessary services and user data, and record restoration steps. This documentation update does not itself clean either TV.

Explicit owner requirement: keep the stock Live TV app, tuner/channel services, TV-input providers and port/input switching on the target TV, even when Live TV is not named in the optional-app keep-list. Verify these functions after cleanup.

## Home/Apps focus navigation refresh

Home and Apps switch pages after 150 ms of stable tab focus; OK is optional. Moving off a tab cancels pending activation. Down commits the focused browse tab immediately before entering the first app row. Search and Settings/Wi-Fi/Inputs still require OK to open. Pending activation is cancelled when the activity pauses. Included in the current working build.

## Favorites checkbox picker refresh

Settings > Home Screen > Edit favorites lists all launchable installed apps except this launcher and reserved recovery launchers. OK toggles a visible checkbox and immediately saves Home membership without launching an app, changing catalogue order, or moving to another card. Unchecked apps are hidden from both Home and Apps, but remain installed and available in the favorites checklist. Membership is read from stored choices, independent of the current page/search or displayed rows; Home shows the first two rows of the selection; Apps shows all selected apps in the same order. Existing default membership is preserved. The latest anydesk.svg is bundled. Included in the current working build.

HDMI alignment refresh: the 19dp header icon now has 7.5dp horizontal padding inside its 34dp circle, centering it without changing the icon size or input behavior. Included in the current working build.

Shared selection correction: Home and Apps use the same saved checkbox choices. Home shows only the first two rows; Apps shows the whole selected list. The favorites checklist always lists installed launchable apps independently, including unchecked ones, so they can be restored. Hiding an app does not uninstall it or disable Live TV/input services. Included in the current working build.

GitHub publication: owner-authorized repository https://github.com/abdemeh/MiOS-Launcher; release v6.10.21 with MiOS-Launcher.apk. Public README includes owner branding, six on-device screenshots, install steps, upstream credit and Downloader placeholder XXXXXXX. APK and original signing key remain local, excluded from Git.
