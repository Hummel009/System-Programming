#include <iostream>
#include "Lib.h"

using namespace std;

int main() {
    int resultAdd = addFunction(5, 3);
    int resultSub = subFunction(8, 4);

    cout << "Addition result: " << resultAdd << ";" << endl;
    cout << "Subtraction result: " << resultSub << ";" << endl;

    printFunction("Hello from the DLL!");

    return 0;
}