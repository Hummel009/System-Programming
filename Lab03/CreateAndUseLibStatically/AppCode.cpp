#include <stdio.h>
#include <windows.h>

typedef int (*AddFunction)(int, int);
typedef int (*SubtractFunction)(int, int);
typedef void (*PrintFunction)(const char*);

int main() {
    HINSTANCE hDLL = LoadLibrary("Lib.dll");
    
    if (hDLL != NULL) {
        AddFunction addFunction = (AddFunction)GetProcAddress(hDLL, "add");
        SubtractFunction subFunction = (SubtractFunction)GetProcAddress(hDLL, "sub");
        PrintFunction printFunction = (PrintFunction)GetProcAddress(hDLL, "print");
        
        if (addFunction && subFunction && printFunction) {
            int resultAdd = addFunction(5, 3);
            int resultSub = subFunction(8, 4);
            
            printf("Addition result: %d\n", resultAdd);
            printf("Subtraction result: %d\n", resultSub);
            
            printFunction("Hello from the DLL!");
        } else {
            printf("Failed to get function pointers\n");
        }
        FreeLibrary(hDLL);
    } else {
        printf("Failed to load DLL\n");
    }

    return 0;
}