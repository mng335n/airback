@echo off
rem -----------------------------------------------------------------------------
rem Start Script for the airback Server
rem -----------------------------------------------------------------------------

if "%OS%" == "Windows_NT" setlocal

call airback.bat --stop

:end
