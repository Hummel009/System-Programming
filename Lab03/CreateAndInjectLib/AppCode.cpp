#include <windows.h>
#include <tlhelp32.h>
#include <iostream>

using namespace std;

DWORD GetProcessIdByProcessName(const string &processName)
{
    HANDLE hSnapshot = CreateToolhelp32Snapshot(TH32CS_SNAPPROCESS, 0);
    PROCESSENTRY32 processEntry;

    ZeroMemory(&processEntry, sizeof(processEntry));
    processEntry.dwSize = sizeof(processEntry);

    DWORD processId = 0;

    if (Process32First(hSnapshot, &processEntry))
    {
        do
        {
            if (processName == processEntry.szExeFile)
            {
                processId = processEntry.th32ProcessID;
                break;
            }
        } while (Process32Next(hSnapshot, &processEntry));
    }

    CloseHandle(hSnapshot);
    return processId;
}

int main()
{
    const string processName = "Process1.exe";
    DWORD pid = GetProcessIdByProcessName(processName);

    if (pid == 0)
    {
        cout << "Process not found." << endl;
        return 1;
    }

    HANDLE hRemoteProcess = OpenProcess(PROCESS_ALL_ACCESS, FALSE, pid);

    if (hRemoteProcess == NULL)
    {
        cout << "Failed to open process." << endl;
        return 1;
    }

    LPVOID threadFunction = (LPVOID)GetProcAddress(GetModuleHandle("kernel32.dll"), "LoadLibraryA");
    string argument = "Lib.dll";

    LPVOID argumentAddress = VirtualAllocEx(hRemoteProcess, NULL, argument.length() + 1, MEM_COMMIT | MEM_RESERVE, PAGE_READWRITE);
    WriteProcessMemory(hRemoteProcess, argumentAddress, argument.c_str(), argument.length() + 1, NULL);

    if (CreateRemoteThread(hRemoteProcess, NULL, 0, (LPTHREAD_START_ROUTINE)threadFunction, argumentAddress, 0, NULL))
    {
        Sleep(1000);
        cout << "Thread created." << endl;
    }
    else
    {
        cout << "Failed to create thread." << endl;
    }

    CloseHandle(hRemoteProcess);
    return 0;
}