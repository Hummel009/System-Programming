#include <windows.h>
#include <tlhelp32.h>
#include <iostream>

using namespace std;

DWORD GetProcessIdByProcessName(const string &processName)
{
    // Создаем снимок процессов
    HANDLE hSnapshot = CreateToolhelp32Snapshot(TH32CS_SNAPPROCESS, 0);
    PROCESSENTRY32 processEntry;

    // Инициализируем структуру PROCESSENTRY32 перед использованием
    ZeroMemory(&processEntry, sizeof(processEntry));
    processEntry.dwSize = sizeof(processEntry);

    DWORD processId = 0;

    // Получаем информацию о первом процессе в снимке
    if (Process32First(hSnapshot, &processEntry))
    {
        do
        {
            // Проверяем совпадение имени процесса
            if (processName == processEntry.szExeFile)
            {
                processId = processEntry.th32ProcessID;
                break;
            }
        } while (Process32Next(hSnapshot, &processEntry));
    }

    // Закрываем дескриптор снимка
    CloseHandle(hSnapshot);
    return processId;
}

int main()
{
    const string processName = "Process1.exe";
    DWORD pid = GetProcessIdByProcessName(processName);

    // Открываем процесс с полным доступом
    HANDLE hRemoteProcess = OpenProcess(PROCESS_ALL_ACCESS, FALSE, pid);

    // Получаем адрес функции LoadLibraryA из kernel32.dll
    LPVOID threadFunction = (LPVOID)GetProcAddress(GetModuleHandle("kernel32.dll"), "LoadLibraryA");
    string argument = "Lib.dll";

    // Выделяем память в удаленном процессе для аргумента строки
    LPVOID argumentAddress = VirtualAllocEx(hRemoteProcess, nullptr, argument.length() + 1, MEM_COMMIT | MEM_RESERVE, PAGE_READWRITE);
    // Записываем аргумент в память удаленного процесса
    WriteProcessMemory(hRemoteProcess, argumentAddress, argument.c_str(), argument.length() + 1, nullptr);

    // Создаем удаленный поток, вызывающий LoadLibraryA с аргументом
    if (CreateRemoteThread(hRemoteProcess, nullptr, 0, (LPTHREAD_START_ROUTINE)threadFunction, argumentAddress, 0, nullptr))
    {
        // Ждем некоторое время, чтобы поток имел шанс выполниться
        Sleep(1000);
        cout << "Thread created." << endl;
    }

    // Закрываем дескриптор удаленного процесса
    CloseHandle(hRemoteProcess);
    return 0;
}