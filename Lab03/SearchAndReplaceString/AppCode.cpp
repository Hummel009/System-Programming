#include <iostream>
#include <Windows.h>

using namespace std;

typedef void (*ReplaceFunction)(const char*, const char*);

int main() {
    HINSTANCE hDLL = LoadLibrary("Lib.dll");

    if (hDLL != NULL) {
        ReplaceFunction replaceFunction = (ReplaceFunction)GetProcAddress(hDLL, "replaceFunction");

        string str = "Previous Text";
        if (replaceFunction) {
            cout << "Before replacement: " << str << ";" << endl;

            replaceFunction("Previous Text", "Replaced Text");

            cout << "After replacement: " << str << ";" << endl;
        } else {
            cout << "Failed to get function pointer!" << endl;
        }

        FreeLibrary(hDLL);
    } else {
        cout << "Failed to load DLL!" << endl;
    }

    return 0;
}