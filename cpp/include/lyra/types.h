#ifndef LYRA_CPP_TYPES
#define LYRA_CPP_TYPES

#include <jni.h>

namespace lyra {
	__attribute__((always_inline)) inline void* address(jlong addr)
	{
		return (void*)(uintptr_t)addr;
	}

	__attribute__((always_inline)) inline jlong address(void* ptr)
	{
		return (uintptr_t)ptr;
	}
}

#endif//LYRA_CPP_TYPES
