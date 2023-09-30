#include <stdio.h>
#include <windows.h>
#include <string>

typedef void (*ReplaceFunction)(const char*, const char*);

int main() {
    HINSTANCE hDLL = LoadLibrary("Lib.dll");

    if (hDLL != NULL) {
        ReplaceFunction replaceFunction = (ReplaceFunction)GetProcAddress(hDLL, "replace");

		std::string s = "Previous Text";
        if (replaceFunction) {
            printf("Before replacement: %s;\n", s.c_str());
			
            replaceFunction("Previous Text", "Replaced Text");

            printf("After replacement: %s;\n", s.c_str());
        } else {
            printf("Failed to get function pointer\n");
        }

        FreeLibrary(hDLL);
    } else {
        printf("Failed to load DLL\n");
    }

    return 0;
}