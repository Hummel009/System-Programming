#include <iostream>
#include <algorithm>
#include <windows.h>
#include <stdio.h>
#include <vector>
#include <fstream>
#include "Lib.h"

using namespace std;

void replaceFunction(const char *data, const char *replacement)
{
	HANDLE process = GetCurrentProcess();
	size_t len = strlen(data);
	size_t replacementLength = strlen(replacement);

	if (process)
	{
		SYSTEM_INFO si;
		GetSystemInfo(&si);

		MEMORY_BASIC_INFORMATION info;
		vector<char> chunk;
		char *p = 0;
		while (p < si.lpMaximumApplicationAddress)
		{
			if (VirtualQueryEx(process, p, &info, sizeof(info)) == sizeof(info))
			{
				if (info.State == MEM_COMMIT && info.AllocationProtect == PAGE_READWRITE)
				{
					p = (char *)info.BaseAddress;
					chunk.resize(info.RegionSize);
					SIZE_T bytesRead;

					try
					{
						if (ReadProcessMemory(process, p, &chunk[0], info.RegionSize, &bytesRead))
						{
							for (size_t i = 0; i < (bytesRead - len); ++i)
							{
								if (memcmp(data, &chunk[i], len) == 0)
								{
									char *ref = (char *)p + i;
									for (int j = 0; j < replacementLength; j++)
									{
										ref[j] = replacement[j];
									}
									ref[replacementLength] = 0;
								}
							}
						}
					}
					catch (bad_alloc &e)
					{
					}
				}
				p += info.RegionSize;
			}
		}
	}
}