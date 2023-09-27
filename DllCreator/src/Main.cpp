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

	__declspec(dllexport) void replaceString(char *target, const char *search, const char *replace)
	{
		MEMORY_BASIC_INFORMATION mbi;
		char *address = 0;

		while (VirtualQuery(address, &mbi, sizeof(mbi)))
		{
			if ((mbi.State == MEM_COMMIT) && (mbi.Protect == PAGE_READWRITE))
			{
				char *start = static_cast<char *>(mbi.BaseAddress);
				char *end = start + mbi.RegionSize;

				while (start < end)
				{
					if (strstr(start, search) != nullptr)
					{
						size_t offset = strstr(start, search) - start;
						memcpy(start + offset, replace, strlen(replace));
					}
					start += mbi.RegionSize;
				}
			}
			address += mbi.RegionSize;
		}
	}
}