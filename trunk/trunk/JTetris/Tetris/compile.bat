@ECHO off
md build
javac -d build tetris/core/TetrisMain.java
cd build
java tetris.core.TetrisMain
pause