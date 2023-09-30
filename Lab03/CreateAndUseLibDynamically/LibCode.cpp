#include <iostream>

using namespace std;

extern "C" {
    __declspec(dllexport) int addFunction(int a, int b) {
        return a + b;
    }

    __declspec(dllexport) int subFunction(int a, int b) {
        return a - b;
    }

    __declspec(dllexport) void printFunction(const char* message) {
        cout << message << endl;
    }
}