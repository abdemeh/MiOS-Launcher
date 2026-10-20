# Editable TV app icons

Replace any SVG here, keeping its filename, then run tools/Build-Moor2Launcher.ps1 and install
the rebuilt APK. apps.json maps Android package names to SVG files, optional display names and
accent colors. The package controls what opens; the picture and name are presentation only.

NoTubeTV (com.ycngmn.notubetv) uses youtube.svg and the label YouTube, as requested. It still
launches NoTubeTV. SmartTube keeps a separate SmartTube-style icon.

Add another SVG and another package entry to customize another installed app. Transparent
backgrounds are best. SVG paths, shapes, fills, gradients and clipping are supported; SVG blur
filters and animations are not. Keep a viewBox. Icons are rendered once and cached on the TV.

Brand SVGs: https://github.com/simple-icons/simple-icons . Original source URLs are in sources.json.
Individual brand licenses and guidelines: https://simpleicons.org and the project's DISCLAIMER.md.
The tivimate, iptv, smarttube and downloader files are custom utility symbols, replaceable here.
App icons supplied by Android remain the fallback when no override exists or an SVG is invalid.
