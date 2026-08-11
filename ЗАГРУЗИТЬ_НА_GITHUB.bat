@echo off
setlocal enabledelayedexpansion

set REPO_DIR=D:\Claude\StepBeat Go\repo
set REMOTE_NAME=origin
set TOKEN=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

cd /d "%REPO_DIR%"
if errorlevel 1 (
    echo ERROR: could not find folder %REPO_DIR%
    pause
    exit /b 1
)
echo Working in: %cd%

if not exist ".git" (
    echo No git repo here yet - creating one...
    git init
    git branch -M main
)

git remote remove %REMOTE_NAME% >nul 2>nul
git remote add %REMOTE_NAME% "https://%TOKEN%@github.com/vysockiymark01-prog/StepBeat-Go.git"

git config user.email "noreply@anthropic.com"
git config user.name "Claude"

echo Adding and committing files...
git add -A
git diff --cached --quiet
if %errorlevel% neq 0 (
    git commit -m "Add StepBeat Go Android app"
) else (
    echo Nothing new to commit.
)

echo Pushing to GitHub (force, this repo only has a placeholder README remotely)...
git push --force -u %REMOTE_NAME% main

echo.
echo Done. If you see the word error or fatal above, copy it and show it to Claude.
pause
