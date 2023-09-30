#include <iostream>
#include "Lib.h"

int main() {
    int resultAdd = add(5, 3);
    int resultSub = sub(8, 4);

    std::cout << "Addition result: " << resultAdd << std::endl;
    std::cout << "Subtraction result: " << resultSub << std::endl;

    print("Hello from the DLL!");

    return 0;
}