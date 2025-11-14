# Script de démarrage du backend SuguConnect avec Java 17 portable
Write-Host "`n========================================" -ForegroundColor Cyan
Write-Host "   DÉMARRAGE BACKEND SUGUCONNECT" -ForegroundColor Cyan
Write-Host "========================================`n" -ForegroundColor Cyan

# Configuration Java 17 portable
$env:JAVA_HOME = "C:\Users\kalandew38\Documents\SuguConnect2\jdk17\jdk-17.0.13+11"
$env:PATH = "$env:JAVA_HOME\bin;$env:PATH"

Write-Host "✅ Java 17 configuré" -ForegroundColor Green
Write-Host "🚀 Démarrage du serveur Spring Boot...`n" -ForegroundColor Yellow

mvn spring-boot:run
