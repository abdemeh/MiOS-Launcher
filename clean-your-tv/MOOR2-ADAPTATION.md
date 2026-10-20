# Xiaomi MiTV-MOOR2 adaptation

Source: https://github.com/Gathin23/cleanyourtv (main, tree fd1ee0a94cada6819660c245c343d167e48365c0, downloaded 2026-10-20).
The downloaded upstream tree and release metadata are preserved under ../review/cleanyourtv/.

Device verified over ADB: MiTV-MOOR2 / venom, Android 11, build RTM5.220609.003.2674.

- Initial Home defaults: SmartTube, Spotify, standard TiviMate, Play Store, Stremio, Netflix.
- Home is one continuous selected-app grid with five columns and automatically growing rows.
  Newly selected apps append in selection order. Unselecting closes the gap. Manual order persists.
  Unselected newly installed apps stay in All apps until chosen for Home.
- A translucent band groups the first row, with three soft ambient glows over a midnight background.
  Glow colour follows the focused app with a smooth tint transition; advanced effects remain off.
- The pencil Edit button opens Choose apps (OK selects/unselects), All apps and Reorder.
  No separate fixed-dock editing step is needed. Reordering works across Home rows.
- All apps is an alphabetical five-column banner grid. OK opens an app; hold OK for Add/Remove
  from Home or App info. Selection mode groups missing shortcuts first and shows checkmarks.
- Other installed apps remain accessible in the App library. Removing a home shortcut does not uninstall it.
- Settings and network controls remain available in the header.
- Wi-Fi opens a custom Network glass panel with connection state, IPv4 address and Choose a network.
  Joining/changing networks opens the TV's native Wi-Fi page, without new permissions.
- Settings opens a custom glass control panel for Picture & Sound, Network, Inputs, installed apps,
  Home appearance and all TV settings. Picture/audio adjustments remain in the native vendor pages.
- Native Xiaomi input player: com.mitv.livetv. HW4 is verified HDMI1.
  Inputs retain their physical names (HDMI 1, HDMI 2, HDMI 3), with a green dot and the HDMI-CEC
  device name separately when connected. No hardcoded console name is retained after disconnect.
  Devices without a reported CEC name show Connected. Open input rows refresh on framework state/info events.
  CEC child entries are omitted when their physical parent input is already listed.
- Optional Home override observes only the Google TV Home activity's window-change events.
  It does not retrieve screen contents or intercept keys. Enabled with the owner's explicit approval
  of Android's Accessibility warning on 2026-10-20.
  Google TV and its setup/recovery wizard remain enabled. Disable the service to restore stock Home.
- No upstream install/debloat script is run automatically. The model's upstream cleanup list is
  provisional, and its setupwraith disable step is excluded from this adaptation.

HDMI1 was verified in Game Enhanced format with HDR Game picture mode, backlight 100,
automatic RGB range/color space, ST.2084 gamma, adaptive luma off, and dark scene boost off.
HDMI-CEC and auto-wake were already enabled. Device auto-off was enabled during this session.
PS5-side HDR calibration and power-link behavior still require the owner's controller and testing.

## Applied cleanup

The owner explicitly approved disabling exactly these packages on 2026-10-20:

```
com.miui.tv.analytics
com.xiaomi.statistic
com.mitv.tvhome.oemtab
com.mitv.tvhome.michannel
com.mitv.tvhome.mitvplus
com.mitv.tvhome.atv
com.xm.webcontent
fusion.android.tv.demo
```

Original states: ../tv-backup/moor2-optional-packages-before.json.
Nothing in this batch is uninstalled. Core TV services, Google TV, setup/recovery, networking,
updates, Play Store, Chromecast and the assistant remain enabled.

## Build and restore

Build: `powershell -NoProfile -ExecutionPolicy Bypass -File tools\Build-Moor2Launcher.ps1`
from the workspace root. JDK, SDK and Gradle cache live under tools/.

Restore stock Home (remove only this adaptation's override):
`powershell -NoProfile -ExecutionPolicy Bypass -File tools\Restore-GoogleTV.ps1`

Restore stock Home and all eight original package states:
`powershell -NoProfile -ExecutionPolicy Bypass -File tools\Restore-GoogleTV.ps1 -RestoreCleanup`

Checks: release build, 117 unit tests (zero failures), Android lint (zero errors).
Six new label tests cover disconnection, missing CEC names, physical port normalization, generic
child labels and changing devices. Physical unplug/replug timing still needs owner testing.
Check records and screenshots are in ../tv-backup/.
The previous installed APK is saved as ../tv-backup/cleanyourtv-before-six-dock.apk.
The version before the multi-row grid/custom-panel redesign is saved as
../tv-backup/cleanyourtv-before-grid-panels.apk.
Device smoke check: temporarily selected Downloader, verified it appended after the existing
shortcuts on row two, then removed that shortcut and restored the prior Home selection.

## Six-favorite Home and blur clock (2026-10-20)

Home lands on one six-app dock. Down animates the dock up to reveal the full installed-app
library, including third-party apps with ordinary launcher entries. Back or Up from the first
row returns Home; Home resets the landing view.

Edit > Edit six favorites: select a dock position, then its replacement from the library.
Edit > Reorder moves apps; the first six are the favorites. Choices persist in the existing
atomic Order store. A temporary SmartTube-to-Deezer replacement survived a cold restart;
SmartTube was restored afterwards. The old five-column preference migrates to six once.

Per the owner's latest direction, photos and photo choices were removed. Home and the clock
screensaver use three procedural soft color orbs; the background follows the focused app,
and the screensaver starts with its last color. Artwork and transitions pause in the background.
Focus now uses 5.5% scale, a 2dp lift, a thin outline and a damped movement without the old
chrome sheen or bounce. Header controls are 34dp high instead of 42dp.

Typography uses the owner's supplied SF Pro Display OTF files from ../tv-backup/fonts.
Regular/Medium/Light/Thin are interface aliases; the large clock uses Display Semibold.
No Rounded or SF Pro Text variants are bundled by this adaptation.

Settings > Home appearance includes date above/below time, a screensaver preview, and a
native Xiaomi screensaver settings shortcut. System idle timing is retained. The selected
Android screensaver is com.gathin.tvhome/.ClockDream. Any remote key exits the preview.

Original screensaver state: ../tv-backup/screensaver-before-photo-dock.txt. Restore with:
`adb shell settings put secure screensaver_components com.google.android.apps.tv.dreamx/.service.Backdrop`
The launcher APK before this change: ../tv-backup/cleanyourtv-before-photo-dock.apk.

Android clears the accessibility override on force-stop. A restart used for testing required
restoring the already approved StockHomeRedirectService; avoid force-stop for routine restarts.
