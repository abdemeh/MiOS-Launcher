$ErrorActionPreference = 'Stop'
$tvWorkspace = Split-Path -Parent $PSScriptRoot
$tvJdk = Get-ChildItem -LiteralPath (Join-Path $PSScriptRoot 'java') -Directory -ErrorAction SilentlyContinue |
    Where-Object { Test-Path -LiteralPath (Join-Path $_.FullName 'bin\java.exe') } |
    Select-Object -First 1
if ($tvJdk) { $env:JAVA_HOME = $tvJdk.FullName }
elseif (-not $env:JAVA_HOME -or -not (Test-Path -LiteralPath (Join-Path $env:JAVA_HOME 'bin/java.exe'))) {
    throw 'JDK 17 is missing. See TRANSFER-GUIDE.md and tools/Configure-LocalBuild.ps1.'
}
$env:GRADLE_USER_HOME = Join-Path $PSScriptRoot 'gradle-cache'
$env:ANDROID_USER_HOME = Join-Path $tvWorkspace '.android'
$env:DEBUG = ''
& python (Join-Path $PSScriptRoot 'prepare_tv_assets.py')
if ($LASTEXITCODE -ne 0) { throw 'TV asset preparation failed.' }
Push-Location (Join-Path $tvWorkspace 'clean-your-tv\launcher')
try {
    & '.\gradlew.bat' assembleRelease testReleaseUnitTest lintRelease --no-daemon --console=plain
    if ($LASTEXITCODE -ne 0) { throw 'Build or checks failed.' }
} finally { Pop-Location }
