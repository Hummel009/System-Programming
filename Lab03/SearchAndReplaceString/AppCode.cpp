#include <iostream>
#include <Windows.h>

using namespace std;

__declspec(dllimport) void replaceFunction(string, string);

int main()
{
	string str = "Previous Text";
	if (replaceFunction)
	{
		cout << "Before replacement: " << str << ";" << endl;
		replaceFunction("Previous Text", "Replaced Text");
		cout << "After replacement: " << str << ";" << endl;
	}
	else
	{
		cout << "Failed to get function pointer!" << endl;
	}
	return 0;
}