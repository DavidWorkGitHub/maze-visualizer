param([switch]$Test, [switch]$Run)
$ErrorActionPreference = 'Stop'
Push-Location $PSScriptRoot
try {
    $compiler = $null
    if ($env:JAVA_HOME -and (Test-Path "$env:JAVA_HOME/bin/javac.exe")) {
        $compiler = "$env:JAVA_HOME/bin/javac.exe"
    } elseif (Get-Command javac -ErrorAction SilentlyContinue) {
        $compiler = (Get-Command javac).Source
    } else {
        $candidate = Get-ChildItem "$env:USERPROFILE/.jdks/*/bin/javac.exe" -ErrorAction SilentlyContinue | Select-Object -First 1
        if ($candidate) { $compiler = $candidate.FullName }
    }
    if (!$compiler) { throw 'Install JDK 17 or newer and set JAVA_HOME to its folder.' }
    $javaBin = Split-Path $compiler
    New-Item -ItemType Directory -Force out | Out-Null
    $sources = @(Get-ChildItem src/maze/*.java | ForEach-Object FullName)
    & $compiler --release 8 -Xlint:-options -d out @sources
    if ($LASTEXITCODE -ne 0) { throw 'Compilation failed.' }
    & "$javaBin/jar.exe" cfe out/maze-visualizer.jar maze.MazeApp -C out maze
    if ($LASTEXITCODE -ne 0) { throw 'Packaging failed.' }
    if ($Test) {
        $tests = @(Get-ChildItem test/maze/*.java | ForEach-Object FullName)
        & $compiler --release 8 -Xlint:-options -cp out -d out/test @tests
        if ($LASTEXITCODE -ne 0) { throw 'Test compilation failed.' }
        & "$javaBin/java.exe" '-Djava.awt.headless=true' -cp 'out;out/test' maze.MazeTests
        if ($LASTEXITCODE -ne 0) { throw 'Tests failed.' }
    }
    if ($Run) { & "$javaBin/java.exe" -jar out/maze-visualizer.jar }
} finally { Pop-Location }
