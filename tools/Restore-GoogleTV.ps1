param([string]$TvAddress = '192.168.1.90:5555', [switch]$RestoreCleanup)
$ErrorActionPreference = 'Stop'
$tvAdb = Join-Path $PSScriptRoot 'platform-tools\adb.exe'
& $tvAdb connect $TvAddress
$tvServices = (& $tvAdb -s $TvAddress shell settings get secure enabled_accessibility_services | Out-String).Trim()
if ($LASTEXITCODE -ne 0) { throw 'Could not read accessibility state.' }
$tvOthers = @($tvServices -split ':' | Where-Object {
    $_ -and $_ -ne 'null' -and $_ -ne 'com.gathin.tvhome/com.gathin.tvhome.StockHomeRedirectService'
})
if ($tvOthers.Count -eq 0) {
    & $tvAdb -s $TvAddress shell settings delete secure enabled_accessibility_services
} else {
    $tvValue = $tvOthers -join ':'
    if ($tvValue -notmatch '^[A-Za-z0-9_./:]+$') { throw 'Unexpected accessibility service value.' }
    & $tvAdb -s $TvAddress shell settings put secure enabled_accessibility_services $tvValue
}
& $tvAdb -s $TvAddress shell pm enable --user 0 com.google.android.apps.tv.launcherx
& $tvAdb -s $TvAddress shell cmd package set-home-activity --user 0 com.google.android.apps.tv.launcherx/.home.HomeActivity
if ($RestoreCleanup) { & (Join-Path $PSScriptRoot 'Set-Moor2Cleanup.ps1') -Restore -TvAddress $TvAddress }
& $tvAdb -s $TvAddress shell input keyevent KEYCODE_HOME
