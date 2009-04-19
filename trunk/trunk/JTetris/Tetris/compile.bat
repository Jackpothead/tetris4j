@ECHO off
md build
md build\sound
md build\image
xcopy /e sound build\sound
xcopy /e image build\image
javac -d build tetris/core/TetrisMain.java
cd build
java tetris.core.TetrisMain
pause