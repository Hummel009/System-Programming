#include <iostream>
#include <Windows.h>

using namespace std;

typedef int (*AddFunction)(int, int);
typedef int (*SubFunction)(int, int);
typedef void (*PrintFunction)(const char *);

int main()
{
    HINSTANCE hDLL = LoadLibrary("Lib.dll");

    if (hDLL != NULL)
    {
        AddFunction addFunction = (AddFunction)GetProcAddress(hDLL, "addFunction");
        SubFunction subFunction = (SubFunction)GetProcAddress(hDLL, "subFunction");
        PrintFunction printFunction = (PrintFunction)GetProcAddress(hDLL, "printFunction");

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