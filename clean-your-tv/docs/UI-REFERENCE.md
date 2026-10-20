# tvOS Home Screen: Visual and Motion Reference for an Android TV Launcher

> **Purpose:** Describe exactly how the Apple TV (tvOS) home screen looks and moves, so an agent can recreate the *look and feel* on Android TV (Jetpack Compose for TV).
> **Note on accuracy:** Values below are close approximations based on observing tvOS behavior, not official Apple numbers. Tune by eye against a real Apple TV. Do NOT use Apple logos, the name "Apple TV/tvOS", SF Pro font files, Apple wallpapers/aerials, or Apple app icons. Recreate the style only.
> Units: dp/sp on a 960x540dp canvas (1920x1080px at 2x). "Focus" = D-pad focus (the equivalent of the Siri Remote highlight).

---

## 1. Overall Impression

- Calm, cinematic, dark. Content is the hero; chrome is minimal.
- Everything is **focus-driven**: nothing is "clicked", things *lift toward you* when highlighted.
- Heavy use of **soft shadows, frosted blur, subtle gradients, and spring physics**.
- No hard borders, no flat Material-style ripples, no elevation outlines. Depth is communicated by scale, shadow, and parallax.
- Motion is slow and floaty (300-600ms), never snappy or bouncy-cartoonish.
- Newer tvOS (26) adds **Liquid Glass**: translucent, refractive, light-catching panels and pills. Emulate with blur + tint + specular edge highlight.

---

## 2. Screen Structure (top to bottom)

```
+------------------------------------------------------------+
|  [top bar: tabs centered, clock/profile right]             |
|                                                            |
|  TOP SHELF (hero / full-bleed content for focused app)     |
|  big artwork, title, subtitle, buttons                     |
|                                                            |
|  [ app row 1: large icons, focused one lifted ]            |
|  [ app row 2 (peeking) ]                                   |
+------------------------------------------------------------+
```

- **Safe area:** 48dp left/right, 27dp top/bottom.
- Background is the focused app's **Top Shelf artwork** (full-bleed), or a wallpaper / blurred gradient when the app has none.

---

## 3. Top Bar

- **Height:** about 56dp, sits over the hero with a top-to-transparent dark gradient.
- **Tab bar:** centered pill group (Home, Apps, Library, Search). In tvOS 26 it is a floating glass capsule.
  - Unfocused tab: translucent text (white 60%), 18sp semibold.
  - Focused tab: a **white (or bright glass) rounded pill** behind dark text, slightly enlarged (scale 1.08), with soft shadow underneath.
  - Selected-but-not-focused tab: white text 100%, no pill.
- **Right cluster:** clock (24h or 12h, 18sp, white 80%), profile avatar circle (32dp), gear icon.
- When focus leaves the top bar, the bar stays visible but non-focused (it does not hide on the Home tab, it only compresses when scrolling down the content).

---

## 4. Top Shelf (Hero Area)

- Occupies about 50-60% of the screen height at rest (when the first row is focused).
- Content for the focused app: large landscape artwork, **full-bleed to screen edges**, with a bottom gradient scrim (transparent to 70% black) so text is legible.
- **Text overlay (bottom-left):**
  - Small app/service label (14sp, white 70%, uppercase optional)
  - **Title** 40sp bold white
  - Subtitle/metadata 16sp white 70%
  - Primary button (white glass pill, dark text, e.g. "Play") + secondary round button (e.g. "+")
- **Variants:** a single big featured item; or a carousel of 3-5 cards that auto-advances every ~6s with a crossfade and page dots.
- **Autoplay:** if the focused app provides a preview, after about 3s of focus a muted video fades in over the artwork (500ms crossfade). Audio is off by default.
- **Transition between apps:** artwork crossfades (about 400ms) while title text fades/slides ~12dp. Never a hard cut.

---

## 5. App Icon Rows

### 5.1 Icon shape and size
- **Shape:** **landscape rounded rectangle**, roughly 16:9 (tvOS uses 400x240px at 1x; **tile ~ 200x120dp** at 960dp width, 6 visible per row at rest in a 960dp screen with ~24dp gaps). Corner radius ~ 12-16dp (continuous / "squircle" curve if possible, e.g. `RoundedCornerShape` with larger radius or a custom smooth corner path).
- First row has **larger icons**; later rows are the same size or slightly smaller.
- Row spacing ~28dp. Next row **peeks** ~25-30% visible at the bottom to hint scrolling.

### 5.2 Icon construction (the layered look)
- tvOS icons are **multi-layer parallax images** (2-5 stacked layers: back, middle, front, plus an optional foreground element).
- At rest the layers are aligned. On focus they shift relative to each other to create depth.
- Recreate: banner = background layer (gradient/blurred color) + foreground layer (app logo/icon, centered, ~50% height). If the app only provides an icon, put it centered on a gradient derived from its dominant color.

### 5.3 Resting state
- Full opacity (not dimmed, but visually "quieter" than focused).
- Faint drop shadow: y 4dp, blur 12dp, black 15-20%.
- **No label** shown.

---

## 6. THE FOCUS EFFECT (most important)

When D-pad focus lands on an icon, all of these happen **together**:

| Effect | Value |
|---|---|
| **Scale** | 1.0 to ~1.10 (spring: damping ratio ~0.75, stiffness ~300, about 350ms) |
| **Lift shadow** | Shadow grows to y ~16-20dp, blur ~32-40dp, 35-45% black. Icon looks like it floats toward you. |
| **Parallax tilt** | Rotate in 3D up to about +/-6 degrees on X and Y (`graphicsLayer { rotationX, rotationY; cameraDistance = 12f * density }`). Tilts **toward the direction of the last move**, then settles to a gentle +/-2 degrees and remains slightly alive (idle micro-motion, 4-6s ease loops). |
| **Layer parallax** | Foreground layers translate up to +/-6dp opposite to the background layers (depth). Back layer moves least, front layer most. |
| **Specular shine** | A soft diagonal white gradient (0 to 25% to 0 alpha, ~30 degree angle, about 40% of tile width) sweeps across the tile once on focus (600ms) and then **follows the tilt** (moves as the tile rotates). |
| **Edge highlight** | 1dp inner border, white 15-25%, brighter along the top edge. |
| **Label** | App name fades in **below** the icon (14-16sp medium, white, 150ms fade, 6dp slide up). Row below gets pushed down slightly (~8dp) to make room. |
| **Neighbors** | Adjacent icons shift outward ~6dp. Non-focused icons in the row dim slightly (to ~85-90% brightness). |
| **Focus out** | Reverse, faster (about 200ms ease-out). |

### Focus movement
- Moving focus **left/right** within a row: the old icon relaxes while the new one lifts. They **overlap in time** (don't wait for one to finish). Animations are **interruptible** at any moment.
- Fast repeated presses (hold D-pad): reduce effect intensity (skip the shine sweep, shorten the spring) to keep up.
- Row horizontal scroll: keep focus around the left third on long rows; smooth `animateScrollTo` with ease-out (about 300ms).
- Moving **down** from row 1: the hero **collapses/fades** (350ms) and rows slide up; moving **up** restores it.

---

## 7. Page and App Transitions

### 7.1 Opening an app
1. Focused icon scales up and **expands to fill the whole screen** (about 400ms, ease-in-out; use shared element / `ActivityOptions.makeScaleUpAnimation` or `makeCustomAnimation`).
2. The rest of the UI fades and blurs out (300ms).
3. App content fades in over the expanded icon color.

### 7.2 Returning home
- Reverse: the app shrinks back into the icon's position (350ms); the home screen fades/unblurs in; the icon lands in focus state.

### 7.3 Tab switching
- Content crossfades (250ms) with a 12dp vertical slide.
- When focus enters the tab bar, the content below **dims to ~70% and blurs slightly (2-4dp)**.

### 7.4 Back button
- Back from a lower row: returns to row 1, hero restored.
- Back from row 1: focus goes to the tab bar.

---

## 8. Edit Mode (rearranging apps)

- Trigger: **long-press Select** on an icon.
- All icons start **jiggling**: rotation +/-1.5 degrees, ~120ms loop, random phase per icon.
- The grabbed icon scales ~1.15, floats above with a heavy shadow.
- D-pad moves it; neighbors animate out of the way (spring, ~300ms).
- A small context menu (glass card) appears on Menu key: **Move to folder, Remove, Done**.
- Dropping an icon onto another creates a **folder**: a glass rounded square with a 3x3 mini grid of the icons inside. Opening it shows a **full-screen blurred modal** with the apps inside, same focus effects.
- Select/Back exits edit mode; jiggle stops with a quick settle animation.

---

## 9. Control Center / Quick Panel

- Opened by a Menu/Home-long-press action (or profile icon).
- A **frosted-glass panel** slides down (or fades+scales) from the top-right: width ~ 360dp, radius 24dp, blur ~30dp, 60% dark tint, 1dp white 12% border.
- Contents: profile switcher (circular avatars), Wi-Fi, Bluetooth, Sleep, Do Not Disturb, sound/output, search.
- Items are rounded buttons (circle or pill). Focus state: **white fill, dark icon, scale 1.1**.
- Background content dims behind it.

---

## 10. Settings

- Opens full screen: wallpaper blurred and darkened (85% black overlay), the panel slides up (350ms, ease-out-expo) or fades in.
- **Two-pane layout:**
  - Left (~30%): large category list, each row 56dp high, 18sp semibold, icon + label.
  - Right: options for the selected category.
- **List rows:** full-width rounded rect (16dp radius), left icon, title 18sp, right-aligned value in gray (with chevron) or a toggle.
  - Unfocused: transparent or white 8% fill, white text.
  - **Focused: solid white fill, dark text, scale ~1.03, soft shadow.** Value text becomes dark gray.
- **Toggles:** pill switch (green when on), spring thumb animation.
- **Sliders:** thick rounded track, thumb enlarges on focus.
- **Categories:** Network, Display & Sound, Wallpaper/Screensaver, Home Screen (icon size, rows, top shelf on/off, autoplay), Apps, Accessibility (reduce motion, larger text, high contrast), Sounds, System, About.
- **Sub-pages** push in with a horizontal slide (30dp) + crossfade (250ms); Back slides out reversed.
- **Dialogs/Alerts:** centered glass card (radius 28dp), title 24sp, body 16sp, two stacked full-width pill buttons; primary is white-filled when focused.

---

## 11. Screensaver / Wallpaper

- Slow, full-screen aerial video loops (use your own licensed footage) with a time/clock label that drifts in and out at the bottom-left in large light text.
- Activates after idle time; any button press fades it out (500ms) and returns to the home screen.
- Home wallpaper: either the focused app's artwork or a gradient / blurred dynamic color field that changes with the focused app.

---

## 12. Design Tokens

**Color**
- Base: `#0B0B0F` near black
- Glass tint: `rgba(20,20,25,0.60)`
- Glass border: `rgba(255,255,255,0.12)`
- Text primary: `#FFFFFF`, secondary: `rgba(255,255,255,0.70)`, tertiary: `rgba(255,255,255,0.50)`
- Focused fill: `#FFFFFF` with text `#111111`
- Toggle on: `#34C759`-like green (choose your own shade)
- Accent: derived dynamically from focused app artwork (Palette API)

**Typography** (use a similar license-free font like Inter)
- Display/title 40sp bold; section title 24sp semibold; tab 18sp semibold; label 14-16sp medium; body 16sp regular
- Slight negative letter-spacing (-0.2%) on large titles

**Shape**
- Tiles 12-16dp, panels 24-28dp, buttons/tabs fully rounded (pill)

**Shadows**
- Rest: y4 / blur12 / 20%
- Focus: y18 / blur36 / 40%
- Panel: y20 / blur60 / 35%

**Motion**
- Springs preferred (damping 0.75, stiffness 300) for focus
- Ease-out-expo for panel entrances
- Durations: micro 150-200ms, standard 250-400ms, large 500-600ms
- All animations interruptible
- **Reduce Motion setting:** disable tilt, parallax, shine, jiggle; use 150ms crossfades only

**Sound (optional, toggle in settings)**
- Very soft, low-volume tick on focus move; softer "pop" on select.

---

## 13. Android TV Implementation Mapping

| tvOS behavior | Android TV approach |
|---|---|
| Landscape layered icon | `android:banner` + Coil; build parallax layers from the banner/icon; custom Composable |
| Focus lift | `Modifier.onFocusChanged` + `animateFloatAsState(spring)` + `graphicsLayer { scaleX, scaleY, rotationX, rotationY, shadowElevation }` |
| Soft big shadow | Custom drawn blurred shadow (`drawBehind` with `Paint.setShadowLayer` or blurred bitmap); `shadowElevation` alone looks too hard |
| Shine | `Brush.linearGradient` overlay with animated offset (`Animatable`) tied to tilt |
| Frosted glass | `Modifier.blur` / `RenderEffect.createBlurEffect` (API 31+); pre-blurred bitmap fallback |
| Tab bar | `androidx.tv.material3.TabRow` with custom indicator pill |
| Row/column focus | `TvLazyRow/TvLazyColumn` (tv-foundation) with `FocusRequester` and `pivotOffsets` (0.2f) |
| App launch zoom | `ActivityOptions.makeScaleUpAnimation` or custom window animations |
| Edit mode | Long-press key handling in `onKeyEvent`; `animateItemPlacement`; infinite-transition jiggle |
| Settings | Two-pane Compose screen; deep-link to `Settings.ACTION_*` for system features |
| Top shelf | Hero Composable fed by per-app content providers (e.g. TV Provider API / Watch Next) or your own featured sources |

---

## 14. Performance Rules (TV hardware is weak)
- Animate with `graphicsLayer` only (no layout or recomposition of heavy trees).
- Downsample banners to tile size; keep only one video decoder active.
- Cache blurred backgrounds.
- Avoid animating blur radius in real time; crossfade between two prebuilt blurred bitmaps instead.
- Cap shine and parallax updates to 60fps; skip them on fast D-pad repeats.

---

## 15. Build Order
1. Home intent + app list in a plain grid
2. Focus system and tile effects (Section 6)
3. Top shelf + background crossfade (Section 4)
4. Tab bar (Section 3)
5. Launch and return transitions (Section 7)
6. Edit mode and folders (Section 8)
7. Settings + Quick panel (Sections 9-10)
8. Screensaver, sounds, accessibility, performance (Sections 11-14)

---

## 16. Acceptance Checklist
- [ ] Focused icon visibly floats: scale, big soft shadow, tilt, parallax, shine
- [ ] Label appears only on focus
- [ ] Hero crossfades smoothly with focus changes; no hard cuts
- [ ] Moving focus fast never stutters or leaves stuck animations
- [ ] App launch expands from the icon; return shrinks back to it
- [ ] Glass panels have blur, tint, thin border, rounded corners
- [ ] Settings rows invert to white-on-dark when focused
- [ ] "Reduce Motion" produces simple fades only
- [ ] No Apple trademarks or assets used
