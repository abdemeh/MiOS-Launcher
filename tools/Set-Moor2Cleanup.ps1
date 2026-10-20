param([switch]$Restore, [string]$TvAddress = '192.168.1.90:5555')
$ErrorActionPreference = 'Stop'
$tvAdb = Join-Path $PSScriptRoot 'platform-tools\adb.exe'
$tvRecord = Join-Path $PSScriptRoot '..\tv-backup\moor2-optional-packages-before.json'
# Reviewed against this exact firmware's package dumps and upstream MOOR2 profile.
# No hardware, network, update, setup, launcher, Play Store or media framework packages.
$tvOptionalPackages = @(
    'com.miui.tv.analytics',
    'com.xiaomi.statistic',
    'com.mitv.tvhome.oemtab',
    'com.mitv.tvhome.michannel',
    'com.mitv.tvhome.mitvplus',
    'com.mitv.tvhome.atv',
    'com.xm.webcontent',
    'fusion.android.tv.demo'
)
function Invoke-TvCommand([string[]]$TvArguments) {
    $tvResult = & $tvAdb -s $TvAddress @TvArguments
    if ($LASTEXITCODE -ne 0) { throw ('ADB failed: ' + ($TvArguments -join ' ')) }
    return $tvResult
}
$tvModel = (Invoke-TvCommand @('shell','getprop','ro.product.model') | Out-String).Trim()
if ($tvModel -ne 'MiTV-MOOR2') { throw 'This cleanup is only reviewed for MiTV-MOOR2.' }
if ($Restore) {
    $tvSaved = Get-Content -Raw -LiteralPath $tvRecord | ConvertFrom-Json
    if ($tvSaved.address -ne $TvAddress) { throw 'Recovery record belongs to another address.' }
    foreach ($tvItem in $tvSaved.packages) {
        if ($tvOptionalPackages -notcontains $tvItem.package) { throw 'Unrecognized recovery package.' }
        $tvAction = switch ([int]$tvItem.enabled) {
            0 { 'default-state' } 1 { 'enable' } 2 { 'disable' }
            3 { 'disable-user' } 4 { 'disable-until-used' }
            default { throw 'Unrecognized package state.' }
        }
        Invoke-TvCommand @('shell','pm',$tvAction,'--user','0',$tvItem.package)
    }
    return
}
if (-not (Test-Path -LiteralPath $tvRecord)) {
    $tvStates = foreach ($tvPackage in $tvOptionalPackages) {
        $tvDump = (Invoke-TvCommand @('shell','dumpsys','package',$tvPackage)) -join "`n"
        $tvUser = [regex]::Match($tvDump, 'User 0: [^\r\n]*installed=true[^\r\n]*enabled=(\d+)')
        if (-not $tvUser.Success) { throw ('Cannot record initial state for ' + $tvPackage) }
        [pscustomobject]@{ package = $tvPackage; enabled = [int]$tvUser.Groups[1].Value }
    }
    [pscustomobject]@{
        address = $TvAddress; model = $tvModel
        recorded = (Get-Date).ToString('o'); packages = @($tvStates)
    } | ConvertTo-Json -Depth 4 | Set-Content -LiteralPath $tvRecord -Encoding UTF8
}
foreach ($tvPackage in $tvOptionalPackages) {
    $tvResult = Invoke-TvCommand @('shell','pm','disable-user','--user','0',$tvPackage)
    $tvResult
    if (($tvResult -join '') -notmatch 'new state: disabled-user') {
        throw ('Cleanup stopped; restore command is available. Package: ' + $tvPackage)
    }
}
