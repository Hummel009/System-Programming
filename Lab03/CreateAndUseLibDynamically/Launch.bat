g++ -shared -o Lib.dll LibCode.cpp
g++ AppCode.cpp -o App.exe -L. -lLib
App
pause