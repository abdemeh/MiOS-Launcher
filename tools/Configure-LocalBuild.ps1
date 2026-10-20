param(
    [Parameter(Mandatory=$true)][string]$JdkPath,
    [Parameter(Mandatory=$true)][string]$SdkPath
)
$ErrorActionPreference = 'Stop'
$tvRoot = Split-Path -Parent $PSScriptRoot
$tvJava = (Resolve-Path -LiteralPath $JdkPath).Path
$tvSdk = (Resolve-Path -LiteralPath $SdkPath).Path
if (-not (Test-Path -LiteralPath (Join-Path $tvJava 'bin/java.exe'))) { throw 'JDK path must contain bin/java.exe (JDK 17 required).' }
foreach ($tvPart in @('platforms/android-34/android.jar', 'build-tools/34.0.0/aapt2.exe')) {
    if (-not (Test-Path -LiteralPath (Join-Path $tvSdk $tvPart))) { throw "Missing Android SDK component: $tvPart" }
}
$tvProperties = 'sdk.dir=' + ($tvSdk -replace '\\', '/')
Set-Content -LiteralPath (Join-Path $tvRoot 'clean-your-tv/launcher/local.properties') -Value $tvProperties -Encoding ASCII
$env:JAVA_HOME = $tvJava
$env:ANDROID_HOME = $tvSdk
Write-Host 'Local SDK configured. JAVA_HOME set for this PowerShell session. Run tools/Build-Moor2Launcher.ps1 in this session.'
