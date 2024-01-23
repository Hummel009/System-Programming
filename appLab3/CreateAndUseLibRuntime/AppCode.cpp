#include <iostream>
#include <Windows.h>

using namespace std;

typedef int (*AddFunction)(int, int);
typedef int (*SubFunction)(int, int);
typedef void (*PrintFunction)(string);

int main()
{
    HINSTANCE hDLL = LoadLibrary("Lib.dll");

    if (hDLL != nullptr)
    {
        auto addFunction = (AddFunction)GetProcAddress(hDLL, "addFunction");
        auto subFunction = (SubFunction)GetProcAddress(hDLL, "subFunction");
        auto printFunction = (PrintFunction)GetProcAddress(hDLL, "printFunction");

        if (addFunction && subFunction && printFunction)
        {
            int resultAdd = addFunction(5, 3);
            int resultSub = subFunction(8, 4);

            cout << "Addition result: " << resultAdd << ";" << endl;
            cout << "Subtraction result: " << resultSub << ";" << endl;

            printFunction("Hello from the DLL!");
        }
        else
        {
            cout << "Failed to get function pointers!" << endl;
        }
        FreeLibrary(hDLL);
    }
    else
    {
        cout << "Failed to load DLL!" << endl;
    }
    return 0;
}