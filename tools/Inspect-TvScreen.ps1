param([string]$RemoteCommand = '')
$ErrorActionPreference = 'Stop'
$tvAdb = Join-Path $PSScriptRoot 'platform-tools\adb.exe'
$tvSnapshot = Join-Path $PSScriptRoot '..\tv-backup\current-window.xml'
if ($RemoteCommand) {
    & $tvAdb -s '192.168.1.90:5555' shell $RemoteCommand
    Start-Sleep -Milliseconds 500
}
$tvScreenReady = $false
for ($tvAttempt = 0; $tvAttempt -lt 3; $tvAttempt++) {
    & $tvAdb -s '192.168.1.90:5555' shell rm -f /data/local/tmp/codex-window.xml
    $tvDumpStatus = (& $tvAdb -s '192.168.1.90:5555' shell uiautomator dump /data/local/tmp/codex-window.xml) -join "`n"
    if ($LASTEXITCODE -eq 0 -and $tvDumpStatus -match 'UI hierchary dumped to:') {
        $tvScreenReady = $true
        break
    }
    Start-Sleep -Milliseconds 500
}
if (-not $tvScreenReady) { throw 'Current screen could not be inspected; no old snapshot will be reused.' }
& $tvAdb -s '192.168.1.90:5555' pull /data/local/tmp/codex-window.xml $tvSnapshot
if ($LASTEXITCODE -ne 0) { throw 'Screen retrieval failed' }
[xml]$tvUi = Get-Content -Raw -LiteralPath $tvSnapshot
$tvUi.SelectNodes('//node') | Where-Object {
    $_.text -ne '' -or $_.focused -eq 'true' -or $_.checkable -eq 'true'
} | ForEach-Object {
    '{0} | focus={1} | checked={2} | {3}' -f $_.text,$_.focused,$_.checked,$_.bounds
}
