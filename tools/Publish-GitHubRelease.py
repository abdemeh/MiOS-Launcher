"""Publish the reviewed MiOS release using an existing Git Credential Manager login.
Run only when the owner has requested publication; never stores or prints the credential.
"""
from pathlib import Path
import hashlib
import json
import os
import subprocess
import urllib.error
import urllib.parse
import urllib.request

ROOT = Path(__file__).resolve().parent.parent
REPO = "abdemeh/MiOS-Launcher"
VERSION = "6.10.21"

def main():
    result = subprocess.run(["git", "credential", "fill"], input="protocol=https\nhost=github.com\n\n", text=True, capture_output=True,
                            env={**os.environ, "GIT_TERMINAL_PROMPT": "0", "GCM_INTERACTIVE": "never"}, check=True)
    credential = dict(line.split("=", 1) for line in result.stdout.splitlines() if "=" in line)
    token = credential.get("password")
    if not token:
        raise RuntimeError("Sign into GitHub through Git Credential Manager first.")
    def api(path, method="GET", data=None, binary=None, content_type="application/json"):
        url = path if path.startswith("https://") else "https://api.github.com" + path
        headers = {"User-Agent": "MiOS-Launcher-release", "Accept": "application/vnd.github+json", "Authorization": "Bearer " + token}
        if data is not None:
            binary = json.dumps(data).encode()
        if binary is not None:
            headers["Content-Type"] = content_type
        with urllib.request.urlopen(urllib.request.Request(url, data=binary, headers=headers, method=method), timeout=120) as response:
            return json.load(response)
    base = "/repos/" + REPO
    head = subprocess.check_output(["git", "-c", "safe.directory=" + str(ROOT), "rev-parse", "HEAD"], cwd=ROOT, text=True).strip()
    remote = api(base + "/commits/main")
    if remote["sha"] != head:
        raise RuntimeError("Push the reviewed commit to main before publishing.")
    apk = ROOT / "artifacts" / ("MiOS-Launcher-" + VERSION + ".apk")
    checks = ROOT / "artifacts/SHA256SUMS.txt"
    report = json.loads((ROOT / "artifacts/verification.json").read_text())
    if hashlib.sha256(apk.read_bytes()).hexdigest() != report["apk_sha256"] or report["failures"] or report["errors"] or report["lint_errors"]:
        raise RuntimeError("APK integrity or validation failed.")
    notes = (ROOT / "RELEASE-NOTES.md").read_text(encoding="utf-8")
    notes += "\nValidation: " + str(report["tests"]) + " passing tests, zero lint errors. SHA-256 checksums attached.\n"
    try:
        release = api(base + "/releases/tags/v" + VERSION)
        if not release["draft"]:
            raise RuntimeError("This release is already public; refusing to replace it silently.")
    except urllib.error.HTTPError as error:
        if error.code != 404:
            raise
        release = api(base + "/releases", "POST", {"tag_name": "v" + VERSION, "target_commitish": head, "name": "MiOS Launcher " + VERSION, "body": notes, "draft": True, "prerelease": False})
    upload = release["upload_url"].split("{", 1)[0]
    for path, name, mime in [(apk, "MiOS-Launcher.apk", "application/vnd.android.package-archive"), (checks, "SHA256SUMS.txt", "text/plain")]:
        if any(asset["name"] == name for asset in release.get("assets", [])):
            raise RuntimeError("Draft already has this asset; inspect it before retrying: " + name)
        asset = api(upload + "?name=" + urllib.parse.quote(name), "POST", binary=path.read_bytes(), content_type=mime)
        if asset["size"] != path.stat().st_size:
            raise RuntimeError("Uploaded asset size mismatch: " + name)
    api(base, "PATCH", {"description": "Apple TV-inspired Android TV launcher with blurred themes, customizable app grids and a clock screensaver. Based on CleanYourTV."})
    api(base + "/topics", "PUT", {"names": ["android-tv", "google-tv", "launcher", "xiaomi", "android-tv-launcher", "screensaver", "customization"]})
    release = api(base + "/releases/" + str(release["id"]), "PATCH", {"draft": False, "make_latest": "true"})
    print(json.dumps({"release": release["html_url"], "tag": release["tag_name"], "assets": [{"name": asset["name"], "url": asset["browser_download_url"]} for asset in release["assets"]]}, indent=2))

if __name__ == "__main__":
    main()
