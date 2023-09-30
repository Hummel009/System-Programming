#include <stdio.h>
#include <iostream>
#include <conio.h>
#include <windows.h>
#include <tlhelp32.h>

using namespace std;

DWORD GetProcessIdByProcessName(string processName)
{
	DWORD processId = 0;
	HANDLE hSnapshot = CreateToolhelp32Snapshot(TH32CS_SNAPPROCESS, 0);
	PROCESSENTRY32 processEntry;

	ZeroMemory(&processEntry, sizeof(processEntry));
	processEntry.dwSize = sizeof(processEntry);

	bool isFound = false;
	while (Process32Next(hSnapshot, &processEntry) && !isFound)
	{
		if (!processName.compare(processEntry.szExeFile))
		{
			processId = processEntry.th32ProcessID;
			isFound = true;
		}
	}

	return processId;
}

int main()
{
	DWORD pid = GetProcessIdByProcessName("Process1.exe");
	cout << "PID: " << pid << endl;

	HANDLE hRemoteProcess = OpenProcess(PROCESS_ALL_ACCESS, FALSE, pid);

	cout << "\nPID finded" << endl;

	LPVOID threadFunction = (LPVOID)GetProcAddress(GetModuleHandle("kernel32.dll"), "LoadLibraryA");
	string argument("Lib.dll");

	LPVOID argumentAddress = VirtualAllocEx(hRemoteProcess, NULL, argument.length() + 1, MEM_COMMIT | MEM_RESERVE, PAGE_READWRITE);

	WriteProcessMemory(hRemoteProcess, (LPVOID)argumentAddress, argument.c_str(), argument.length() + 1, NULL);

	if (CreateRemoteThread(hRemoteProcess, NULL, 0, (LPTHREAD_START_ROUTINE)threadFunction, (LPVOID)argumentAddress, 0, NULL))
	{
		Sleep(1000);
		cout << "Creating thread" << endl;
		CloseHandle(hRemoteProcess);
	}
	else
		cout << "Cant create thread" << endl;
	return 0;
}