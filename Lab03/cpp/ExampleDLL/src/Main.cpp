#include <iostream>
#include <Windows.h>

extern "C"
{

	__declspec(dllexport) int add(int a, int b)
	{
		return a + b;
	}

	__declspec(dllexport) int sub(int a, int b)
	{
		return a - b;
	}

	__declspec(dllexport) void print(const char *message)
	{
		std::cout << message << std::endl;
	}
}