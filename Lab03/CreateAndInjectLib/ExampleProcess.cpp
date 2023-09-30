#include <stdio.h>
#include <iostream>
#include <conio.h>
#include <Windows.h>

using namespace std;

int main()
{
	string localString = "Hummel009's Process";

	while (1)
	{
		cout << localString.c_str() << endl;
		_getch();
	}
}