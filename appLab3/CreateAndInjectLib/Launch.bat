g++ Process.cpp -o Process1.exe
g++ Process.cpp -o Process2.exe
start cmd /k "Process1.exe"
start cmd /k "Process2.exe"

g++ -shared -o Lib.dll LibCode.cpp
g++ AppCode.cpp -o App.exe
App
pause