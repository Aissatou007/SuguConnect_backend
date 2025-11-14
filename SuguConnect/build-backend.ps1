# Script de compilation du backend SuguConnect avec Java 17 portable
Write-Host "`n========================================" -ForegroundColor Cyan
Write-Host "   COMPILATION BACKEND SUGUCONNECT" -ForegroundColor Cyan
Write-Host "========================================`n" -ForegroundColor Cyan

# Configuration Java 17 portable
$env:JAVA_HOME = "C:\Users\kalandew38\Documents\SuguConnect2\jdk17\jdk-17.0.13+11"
$env:PATH = "$env:JAVA_HOME\bin;$env:PATH"

Write-Host "✅ Java 17 configuré" -ForegroundColor Green
java -version
Write-Host ""

# Compilation
Write-Host "🔨 Compilation en cours..." -ForegroundColor Yellow
mvn clean install -DskipTests

if ($LASTEXITCODE -eq 0) {
    Write-Host "`n✅ COMPILATION RÉUSSIE !" -ForegroundColor Green
    Write-Host "`nPour démarrer le backend :" -ForegroundColor Cyan
    Write-Host "  .\run-backend.ps1" -ForegroundColor White
} else {
    Write-Host "`n❌ ERREUR DE COMPILATION" -ForegroundColor Red
    Write-Host "Vérifiez les erreurs ci-dessus" -ForegroundColor Yellow
}
