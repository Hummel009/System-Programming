#include <iostream>
#include <algorithm>
#include <windows.h>
#include <stdio.h>
#include <vector>
#include <fstream>
#include "Lib.h"

using namespace std;

void replaceFunction(string data, string replacement)
{
    HANDLE process = GetCurrentProcess();
    SYSTEM_INFO si;
    GetSystemInfo(&si);

    // Структура для хранения информации о памяти
    MEMORY_BASIC_INFORMATION info;

    // Указатель на текущий адрес в памяти
    char *p = nullptr;

    // Проходим по всей памяти процесса
    while (p < si.lpMaximumApplicationAddress)
    {
        // Получаем информацию о текущем блоке памяти
        if (VirtualQueryEx(process, p, &info, sizeof(info)) == sizeof(info))
        {
            // Проверяем, что блок памяти выделен и доступен для записи
            if (info.State == MEM_COMMIT && info.AllocationProtect == PAGE_READWRITE)
            {
                // Выделяем буфер для хранения данных текущего блока памяти
                vector<char> chunk(info.RegionSize);

                // Считываем данные из текущего блока памяти
                SIZE_T bytesRead;
                if (ReadProcessMemory(process, p, &chunk[0], info.RegionSize, &bytesRead))
                {
                    // Проходим по данным и ищем совпадение с подстрокой 'data'
                    for (size_t i = 0; i < (bytesRead - data.length()); ++i)
                    {
                        if (equal(data.begin(), data.end(), &chunk[i]))
                        {
                            // Найдено совпадение, заменяем подстроку
                            char *ref = static_cast<char *>(p) + i;
                            copy(replacement.begin(), replacement.end(), ref);
                            ref[replacement.length()] = 0;  // Добавляем нулевой символ в конец замененной строки
                        }
                    }
                }
            }
            // Переходим к следующему блоку памяти
            p += info.RegionSize;
        }
    }
}

BOOL APIENTRY DllMain(HMODULE hModule, DWORD ul_reason_for_call, LPVOID lpReserved)
{
    switch (ul_reason_for_call)
    {
    case DLL_PROCESS_ATTACH:
        replaceFunction("Hummel010", "Hummel009");
        break;
    case DLL_THREAD_ATTACH:
    case DLL_THREAD_DETACH:
    case DLL_PROCESS_DETACH:
        break;
    }
    return TRUE;
}