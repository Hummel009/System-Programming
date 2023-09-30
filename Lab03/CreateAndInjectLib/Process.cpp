#include <stdio.h>
#include <iostream>
#include <conio.h>
#include <Windows.h>

using namespace std;

int main()
{
	string localString = "Hummel010";

	while (1)
	{
		cout << localString.c_str() << endl;
		_getch();
	}
}