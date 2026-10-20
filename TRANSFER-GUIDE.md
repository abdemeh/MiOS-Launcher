# Transfer to another PC or coding agent

An MD file provides instructions and context; it does not contain the source, artwork, signing key or development tools. Give the agent the extracted project folder together with this guide. This applies to Antigravity or any editor/agent that can work with local files and run PowerShell commands. No editor-specific integration is required.

## Copy or clone the project

For installation only, copy artifacts/MiOS-Launcher-6.10.21.apk. For development, copy/clone the source repository and read AGENTS.md, PROJECT-STATE.md and this guide. The obsolete v0.1 releases and source ZIPs were removed at the owner's request on 2026-10-21.

Git excludes .android, tv-backup, local SDK/JDK/ADB binaries, caches, build outputs and APKs. To build updates for an existing installation, privately transfer .android/debug.keystore from this PC to the same relative path on the new PC before building. Do not commit this key. The repository alone cannot reproduce the existing signing identity. For a new independent installation a new key is possible, but it cannot update an app signed with another key.

The tools below are installed separately on a new PC. Configure the local SDK path for its actual location. The TV's app preferences, streaming apps and credentials are not included in Git.

## Windows build setup

For a source-only copy, install Python 3 (python command), JDK 17, and an Android SDK with platform android-34, Build Tools 34.0.0, and Platform Tools (adb). Android Studio's SDK Manager can install the Android components. Accept the SDK licenses. The Gradle wrapper downloads Gradle 8.9 and project dependencies on first build, so internet access is required. Android Gradle Plugin is pinned to 8.5.2 in the source.

In PowerShell at the extracted project root, use your actual installed paths:

```powershell
powershell.exe -NoProfile -ExecutionPolicy Bypass
.\tools\Configure-LocalBuild.ps1 -JdkPath 'C:\path\to\jdk-17' -SdkPath 'C:\path\to\Android\Sdk'
.\tools\Build-Moor2Launcher.ps1
```

Run both in the same PowerShell session so JAVA_HOME is retained. If a project-local tools/java JDK exists, the build script prefers it. Configure-LocalBuild writes this PC's SDK path to local.properties. The existing .android/debug.keystore must be present before building; otherwise Android may create a different key. Build output: clean-your-tv/launcher/app/build/outputs/apk/release/app-release.apk. Build includes release unit tests and lint. artifacts is the last verified installation package; it is not automatically overwritten by a normal build. Copy the validated build output there when publishing a local artifact.

## Update the TV from the new PC

Use the same LAN, enable TV debugging and accept the new PC's authorization prompt on the TV. The old PC's authorization does not transfer. Replace adb below with the full path to your SDK's platform-tools/adb.exe if it is not on PATH. The TV's last known IP was 192.168.1.90; check its current IP first.

```powershell
adb connect 192.168.1.90:5555
adb -s 192.168.1.90:5555 install -r .\clean-your-tv\launcher\app\build\outputs\apk\release\app-release.apk
adb -s 192.168.1.90:5555 shell am start -n com.gathin.tvhome/.MainActivity -a android.intent.action.MAIN -c android.intent.category.HOME
```

Use install -r to retain app settings; do not uninstall to resolve a signature mismatch. Recover the original signing key instead. This is a native launcher APK, not replacement TV firmware. The source repository does not contain installed streaming apps, credentials, or the TV's launcher preferences. Updating this same TV preserves its preferences; another TV requires fresh setup and compatibility checks. Do not blindly run the MOOR2 cleanup scripts on another model.

## Prompt to give the new agent

> This folder contains my Xiaomi MiTV-MOOR2 Android 11 launcher project, which I may also install on another TV. Read AGENTS.md, PROJECT-STATE.md, TRANSFER-GUIDE.md, and clean-your-tv/docs/UI-IMPLEMENTATION.md. Continue from the current source. Preserve the original signing key and app preferences. Check local tools and configure the SDK path before building. Also clean the target TV using the cleanup workflow below: I will give you the list of apps to keep; remove other unneeded user apps, disable or remove verified unnecessary Xiaomi promotional/analytics apps, and clean disposable junk. First identify the target model and required services; preserve the launcher and dependencies needed by the kept apps and TV hardware. Do not root/flash or force-stop the launcher. Tell me what you change and validate builds with the supplied script. Editable logos are in assets/app-icons; active mappings are assets/app-icons/apps.json. Do not assume old conversation history is available.

## Cleanup task for the receiving agent

The owner requests cleanup on the target TV in addition to installing the launcher. Ask for the keep-list if it has not yet been provided, then map the owner's app names to actual installed package IDs. Do not reuse the first TV's keep-list or package assumptions automatically. The keep-list controls optional apps; required system services and dependencies must also be retained. The owner explicitly requires keeping stock Live TV and its broadcast/tuner/port-input functionality even if Live TV is omitted from their optional-app keep-list.

1. Connect to the owner's intended TV and record its model, Android version, installed packages, enabled/disabled states and free storage. Save recovery records locally before changes. Identify which optional Xiaomi apps supply advertisements, recommendations, analytics, retail demos or redundant launchers on this exact model.
2. Once the keep-list is supplied, uninstall unneeded third-party apps outside that list. Preserve their data where supported unless the owner explicitly asks to erase it. For verified unnecessary preinstalled Xiaomi or bundled apps, prefer disabling or uninstalling for the current Android user while preserving the system APK and recording restore commands. Do not delete system partitions or treat every package containing Xiaomi/Mi/TV as junk.
3. Preserve com.gathin.tvhome and its Home override/screensaver, a working recovery launcher/setup path, remote and input services, the stock Live TV app and its tuner/channel/TV-input dependencies (including antenna/cable channels and AV/HDMI port access), HDMI/HDR/CEC, display and picture controls, audio/Bluetooth, Wi-Fi/Ethernet, DRM/media codecs, updates, Play Store and dependencies needed by retained apps. If a package's role is uncertain, inspect it and leave it installed until its purpose is established. The old eight-package MOOR2 cleanup list is a historical reference, not a universal removal list.
4. Reclaim disposable app caches using supported Android mechanisms. Delete only specifically identified temporary downloads, obsolete installer APKs or agent-created files that are no longer needed. Check exact paths and contents first. Keep personal files, IPTV playlists, downloads the owner still uses, credentials, app settings and databases; do not run blanket storage deletion or clear app data as a cache cleanup shortcut.
5. Work in small batches and check Home navigation, kept apps, Play Store, networking, remote/volume, Live TV launch/channel access, input switching and HDMI playback afterward. Record each removed/disabled package, original state, restoration command, deleted temporary file and storage recovered. Report any packages retained because the TV or a kept app requires them. Do not claim a complete device/app-data backup when only package state was saved.

This section records the owner's requested task for the receiving agent; that agent must still follow its environment's actual permissions. Do the read-only inventory while awaiting the owner's keep-list, and perform cleanup once that list is available. Do not uninstall optional apps based on a guessed list.

## Current artwork

The latest downloader.svg, speedtest.svg and anydesk.svg are integrated into the active artwork and bundled APK. Downloader uses a vibrant #F37623 orange card; AnyDesk uses a white card and the supplied red logo, mapped to com.anydesk.anydeskandroid. Copy this refreshed APK to the other TV to get those designs. The AnyDesk app itself is not bundled or installed by the launcher; it appears when the installed app is discovered. Old release snapshots were removed at the owner's request.

## Home/Apps focus navigation refresh

Home and Apps switch pages after 150 ms of stable tab focus; OK is optional. Moving off a tab cancels pending activation. Down commits the focused browse tab immediately before entering the first app row. Search and Settings/Wi-Fi/Inputs still require OK to open. Pending activation is cancelled when the activity pauses. Included in the current working build.

## Favorites checkbox picker refresh

Settings > Home Screen > Edit favorites lists all launchable installed apps except this launcher and reserved recovery launchers. OK toggles a visible checkbox and immediately saves Home membership without launching an app, changing catalogue order, or moving to another card. Unchecked apps are hidden from both Home and Apps, but remain installed and available in the favorites checklist. Membership is read from stored choices, independent of the current page/search or displayed rows; Home shows the first two rows of the selection; Apps shows all selected apps in the same order. Existing default membership is preserved. The latest anydesk.svg is bundled. Included in the current working build.

HDMI alignment refresh: the 19dp header icon now has 7.5dp horizontal padding inside its 34dp circle, centering it without changing the icon size or input behavior. Included in the current working build.

Shared selection correction: Home and Apps use the same saved checkbox choices. Home shows only the first two rows; Apps shows the whole selected list. The favorites checklist always lists installed launchable apps independently, including unchecked ones, so they can be restored. Hiding an app does not uninstall it or disable Live TV/input services. Included in the current working build.
