"""Package editable logos and prerender the supplied aurora recipe; standard library only."""
from pathlib import Path
import json
import base64
import copy
import math
import shutil
import struct
import zlib
import xml.etree.ElementTree as ET

ROOT = Path(__file__).resolve().parent.parent
APP = ROOT / "clean-your-tv/launcher/app/src/main"


def rgb(value):
    return tuple(int(value[i:i + 2], 16) for i in (1, 3, 5))


def box_blur(pixels, width, height, radius, horizontal):
    output = [None] * len(pixels)
    length, lines = (width, height) if horizontal else (height, width)
    def index(line, pos):
        return line * width + pos if horizontal else pos * width + line
    divisor = radius * 2 + 1
    for line in range(lines):
        sums = [0.0, 0.0, 0.0]
        for offset in range(-radius, radius + 1):
            color = pixels[index(line, max(0, min(length - 1, offset)))]
            for channel in range(3):
                sums[channel] += color[channel]
        for pos in range(length):
            output[index(line, pos)] = tuple(value / divisor for value in sums)
            before = pixels[index(line, max(0, pos - radius))]
            after = pixels[index(line, min(length - 1, pos + radius + 1))]
            for channel in range(3):
                sums[channel] += after[channel] - before[channel]
    return output


def generate_background(recipe, target):
    width, height = 480, 270
    stops = recipe["background"]["baseGradient"]["stops"]
    stops = [(s["position"], rgb(s["color"])) for s in stops]
    angle = math.radians(recipe["background"]["baseGradient"]["angleDegrees"])
    vx, vy = math.cos(angle), math.sin(angle)
    low, high = min(0, vx) + min(0, vy), max(0, vx) + max(0, vy)
    blobs = [(rgb(b["color"]), b) for b in recipe["background"]["lightBlobs"]]
    pixels = []
    for y in range(height):
        py = y / (height - 1)
        for x in range(width):
            px = x / (width - 1)
            position = (px * vx + py * vy - low) / (high - low)
            segment = next((i for i in range(len(stops) - 1) if position <= stops[i + 1][0]), len(stops) - 2)
            start, end = stops[segment], stops[segment + 1]
            blend = (position - start[0]) / (end[0] - start[0])
            color = [start[1][i] * (1 - blend) + end[1][i] * blend for i in range(3)]
            for light, blob in blobs:
                distance = ((px - blob["centerX"]) / blob["radiusX"]) ** 2 + ((py - blob["centerY"]) / blob["radiusY"]) ** 2
                opacity = blob["opacity"] * math.exp(-4 * distance)
                color = [255 - (255 - color[i]) * (1 - light[i] * opacity / 255) for i in range(3)]
            pixels.append(tuple(color))
    radius = round(recipe["blur"]["radiusPxAt1080p"] / 12)
    if recipe["blur"]["enabled"]:
        for _ in range(3):
            pixels = box_blur(pixels, width, height, radius, True)
            pixels = box_blur(pixels, width, height, radius, False)
    vignette = recipe["overlays"]["vignette"]
    bottom = recipe["overlays"]["bottomDarkening"]
    for y in range(height):
        py = y / (height - 1)
        for x in range(width):
            px = x / (width - 1)
            color = pixels[y * width + x]
            if vignette["enabled"]:
                distance = math.hypot((px - .5) * 1.4, (py - .5) * 1.4)
                mix = min(1, max(0, distance / vignette["radius"])) ** 2 * vignette["strength"]
                tint = rgb(vignette["color"])
                color = tuple(color[i] * (1 - mix) + tint[i] * mix for i in range(3))
            if bottom["enabled"]:
                mix = min(1, max(0, (py - bottom["startY"]) / (bottom["endY"] - bottom["startY"])))
                mix = mix * mix * (3 - 2 * mix) * bottom["endOpacity"]
                tint = rgb(bottom["color"])
                color = tuple(color[i] * (1 - mix) + tint[i] * mix for i in range(3))
            # Recover light-field contrast after the soft blur, retaining a dark navy floor.
            luminance = color[0] * .2126 + color[1] * .7152 + color[2] * .0722
            color = tuple(max(0, min(255, (luminance + (value - luminance) * 1.15 - 10) * 1.35)) for value in color)
            pixels[y * width + x] = color
    out_width, out_height = 1920, 1080
    rows = []
    for y in range(height):
        row = bytearray(out_width * 3)
        for x in range(out_width):
            source = x * (width - 1) / (out_width - 1)
            left, fraction = int(source), source % 1
            a, b = pixels[y * width + left], pixels[y * width + min(width - 1, left + 1)]
            for c in range(3):
                row[x * 3 + c] = min(255, max(0, round(a[c] * (1 - fraction) + b[c] * fraction)))
        rows.append(row)
    data = bytearray()
    for y in range(out_height):
        source = y * (height - 1) / (out_height - 1)
        top, fraction = int(source), source % 1
        a, b = rows[top], rows[min(height - 1, top + 1)]
        data.append(0)
        data.extend(round(a[i] * (1 - fraction) + b[i] * fraction) for i in range(out_width * 3))
    def chunk(kind, contents):
        return struct.pack("!I", len(contents)) + kind + contents + struct.pack("!I", zlib.crc32(kind + contents) & 0xffffffff)
    target.parent.mkdir(parents=True, exist_ok=True)
    target.write_bytes(b"\x89PNG\r\n\x1a\n" + chunk(b"IHDR", struct.pack("!2I5B", out_width, out_height, 8, 2, 0, 0, 0)) + chunk(b"IDAT", zlib.compress(data, 7)) + chunk(b"IEND", b""))


icons = ROOT / "assets/app-icons"
mapping = json.loads((icons / "apps.json").read_text(encoding="utf-8-sig"))
bundled = APP / "assets/app-icons"
bundled.mkdir(parents=True, exist_ok=True)
packaged = copy.deepcopy(mapping)
for package, choice in mapping.items():
    filename = choice["icon"]
    if Path(filename).name != filename or not filename.endswith(".svg"):
        raise ValueError(f"Invalid SVG filename for {package}")
    tree = ET.parse(icons / filename)
    root = tree.getroot()
    embedded = [element for element in root.iter() if element.tag.split("}")[-1] == "image"]
    if len(embedded) == 1:
        href = embedded[0].get("href", embedded[0].get("{http://www.w3.org/1999/xlink}href", ""))
        if not href.startswith("data:image/png;base64,"):
            raise ValueError(f"Only local embedded PNG artwork is supported: {filename}")
        png_name = Path(filename).stem + ".png"
        (bundled / png_name).write_bytes(base64.b64decode(href.split(",", 1)[1], validate=True))
        packaged[package]["icon"] = png_name
    else:
        if "viewBox" not in root.attrib:
            root.set("viewBox", f"0 0 {root.get('width')} {root.get('height')}")
        tree.write(bundled / filename, encoding="utf-8", xml_declaration=True)
(bundled / "apps.json").write_text(json.dumps(packaged, indent=2), encoding="utf-8")
source = ROOT / "assets/backgrounds/aurora.json"
target = APP / "res/drawable-nodpi/aurora_background.png"
if not target.exists() or max(source.stat().st_mtime, Path(__file__).stat().st_mtime) > target.stat().st_mtime:
    generate_background(json.loads(source.read_text(encoding="utf-8-sig")), target)
print(f"Prepared {len(mapping)} SVG app mappings and the 1920x1080 aurora background.")
