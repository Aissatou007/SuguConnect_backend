# Script pour retirer les fichiers contenant des secrets du commit 711e7a8
cd C:\Users\kalandew38\Documents\SuguConnect2\SuguConnect_backend\SuguConnect

Write-Host "=== Retrait des secrets du commit 711e7a8 ===" -ForegroundColor Yellow

# Sauvegarder les changements actuels
Write-Host "Sauvegarde des changements actuels..." -ForegroundColor Cyan
git stash

# Vérifier que nous sommes sur la bonne branche
$currentBranch = git rev-parse --abbrev-ref HEAD
Write-Host "Branche actuelle: $currentBranch" -ForegroundColor Cyan

# Créer un script pour l'éditeur de rebase
$editorScript = @"
`$file = `$args[0]
`$content = Get-Content `$file -Raw
`$content = `$content -replace '^pick 711e7a8', 'edit 711e7a8'
Set-Content `$file -Value `$content
"@

$editorScriptPath = Join-Path $PSScriptRoot "rebase-editor.ps1"
Set-Content -Path $editorScriptPath -Value $editorScript

# Définir l'éditeur
$env:GIT_EDITOR = "powershell -File `"$editorScriptPath`""

Write-Host "Démarrage du rebase interactif..." -ForegroundColor Cyan
git rebase -i 711e7a8^

# Si le rebase est en mode "edit", retirer les fichiers problématiques
if ($LASTEXITCODE -eq 0 -or (Test-Path ".git/rebase-merge")) {
    Write-Host "Retrait des fichiers contenant des secrets..." -ForegroundColor Cyan
    
    # Retirer les fichiers problématiques du commit
    git reset HEAD~1 -- SuguConnect/fix-secret.ps1 SuguConnect/fix-secret.sh SuguConnect/fix-commit-direct.ps1 SuguConnect/fix-commit-final.ps1 SuguConnect/fix-commit.ps1 2>$null
    
    # Amender le commit
    git commit --amend --no-edit
    
    Write-Host "Continuation du rebase..." -ForegroundColor Cyan
    git rebase --continue
}

# Nettoyer
Remove-Item $editorScriptPath -ErrorAction SilentlyContinue

# Restaurer les changements
Write-Host "Restauration des changements..." -ForegroundColor Cyan
git stash pop

Write-Host "=== Terminé! ===" -ForegroundColor Green
Write-Host "Vous pouvez maintenant faire: git push origin Yacouba --force" -ForegroundColor Yellow

