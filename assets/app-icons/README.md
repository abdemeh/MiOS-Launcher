# Editable app artwork

Replace the matching SVG directly in this folder and rebuild using tools/Build-Moor2Launcher.ps1. apps.json maps package IDs to artwork, display labels and accent colors. App launch targets do not change when artwork is replaced. Use self-contained SVGs with a viewBox and transparent background; local embedded PNG artwork is also supported. No external URLs or SVG blur/animation are needed.

The build copies/normalizes active artwork into the Android app. See ICON-MAP.md for package mappings and sources.json for provenance. App logos remain the property of their respective owners. The launcher's own icon/banner are in assets/branding.
