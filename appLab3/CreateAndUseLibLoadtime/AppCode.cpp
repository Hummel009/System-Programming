#include <iostream>

using namespace std;

__declspec(dllimport) int addFunction(int a, int b);
__declspec(dllimport) int subFunction(int a, int b);
__declspec(dllimport) void printFunction(string message);

int main()
{
    int resultAdd = addFunction(5, 3);
    int resultSub = subFunction(8, 4);

    cout << "Addition result: " << resultAdd << ";" << endl;
    cout << "Subtraction result: " << resultSub << ";" << endl;

    printFunction("Hello from the DLL!");

    return 0;
}