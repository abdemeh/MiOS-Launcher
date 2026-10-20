param([switch]$Restore)
$ErrorActionPreference = 'Stop'
$tvAdb = Join-Path $PSScriptRoot 'platform-tools/adb.exe'
$tvAddress = '192.168.1.90:5555'
$tvBackup = Join-Path $PSScriptRoot '../tv-backup/unused-app-cleanup'
$tvRecord = Join-Path $tvBackup 'record.json'
function Invoke-Tv([string[]]$TvArgs) {
    $tvOutput = & $tvAdb -s $tvAddress @TvArgs
    if ($LASTEXITCODE -ne 0) { throw ('ADB failed: ' + ($TvArgs -join ' ')) }
    return ($tvOutput | Out-String).Trim()
}
if ((Invoke-Tv @('shell','getprop','ro.product.model')) -ne 'MiTV-MOOR2') { throw 'Wrong TV model.' }
$tvUnused = @('com.google.android.youtube.tv','com.google.android.youtube.tvmusic','com.google.android.play.games')
if ($Restore) {
    $tvState = Get-Content -Raw -LiteralPath $tvRecord | ConvertFrom-Json
    foreach ($tvApp in $tvState.apps) {
        if ($tvUnused -notcontains $tvApp.package) { throw "Unexpected recovery package." }
        if ($tvApp.installed) {
            Invoke-Tv @('shell','cmd','package','install-existing','--user','0',$tvApp.package)
            $tvAction = switch ([int]$tvApp.enabled) { 0 {'default-state'} 1 {'enable'} 2 {'disable'} 3 {'disable-user'} 4 {'disable-until-used'} }
            Invoke-Tv @('shell','pm',$tvAction,'--user','0',$tvApp.package)
        }
    }
    return
}
New-Item -ItemType Directory -Path $tvBackup -Force | Out-Null
if (-not (Test-Path -LiteralPath $tvRecord)) {
    Invoke-Tv @('shell','pm','list','packages') | Set-Content -LiteralPath (Join-Path $tvBackup 'packages-before.txt') -Encoding UTF8
    Invoke-Tv @('shell','df','-k','/data') | Set-Content -LiteralPath (Join-Path $tvBackup 'storage-before.txt') -Encoding UTF8
    $tvApps = foreach ($tvPackage in $tvUnused) {
        $tvDump = Invoke-Tv @('shell','dumpsys','package',$tvPackage)
        $tvDump | Set-Content -LiteralPath (Join-Path $tvBackup ($tvPackage + '.txt')) -Encoding UTF8
        $tvUser = [regex]::Match($tvDump, 'User 0: ([^\r\n]+)')
        $tvEnabled = [regex]::Match($tvUser.Value,'enabled=(\d+)')
        [pscustomobject]@{package=$tvPackage;installed=$tvUser.Value.Contains('installed=true');enabled=if($tvEnabled.Success){[int]$tvEnabled.Groups[1].Value}else{0}}
    }
    [pscustomobject]@{address=$tvAddress;model='MiTV-MOOR2';apps=@($tvApps)} | ConvertTo-Json -Depth 4 | Set-Content -LiteralPath $tvRecord -Encoding UTF8
}
$tvSaved = Get-Content -Raw -LiteralPath $tvRecord | ConvertFrom-Json
foreach ($tvApp in $tvSaved.apps) {
    if ($tvUnused -notcontains $tvApp.package) { throw 'Unexpected cleanup package.' }
    if ($tvApp.installed) { Invoke-Tv @('shell','pm','uninstall','-k','--user','0',$tvApp.package) }
}
# Ask Android to reclaim disposable caches; no pm clear or app-data deletion.
Invoke-Tv @('shell','pm','trim-caches','1G')
Invoke-Tv @('shell','df','-k','/data') | Set-Content -LiteralPath (Join-Path $tvBackup 'storage-after.txt') -Encoding UTF8
Invoke-Tv @('shell','pm','list','packages','-3') | Set-Content -LiteralPath (Join-Path $tvBackup 'third-party-after.txt') -Encoding UTF8
