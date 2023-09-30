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

    MEMORY_BASIC_INFORMATION info;
    char *p = 0;

    while (p < si.lpMaximumApplicationAddress)
    {
        if (VirtualQueryEx(process, p, &info, sizeof(info)) == sizeof(info))
        {
            if (info.State == MEM_COMMIT && info.AllocationProtect == PAGE_READWRITE)
            {
                vector<char> chunk(info.RegionSize);

                SIZE_T bytesRead;
                if (ReadProcessMemory(process, p, &chunk[0], info.RegionSize, &bytesRead))
                {
                    for (size_t i = 0; i < (bytesRead - data.length()); ++i)
                    {
                        if (equal(data.begin(), data.end(), &chunk[i]))
                        {
                            char *ref = static_cast<char *>(p) + i;
                            copy(replacement.begin(), replacement.end(), ref);
                            ref[replacement.length()] = 0;
                        }
                    }
                }
            }
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