g++ -shared -o Lib.lib LibCode.cpp
g++ AppCode.cpp -o App.exe -L. -lLib
App
pause