#ifndef LYRA_CPP_TYPES
#define LYRA_CPP_TYPES

#include <stdint.h>
#include <jni.h>

namespace lyra {
	__attribute__((always_inline)) inline void* address(jlong addr)
	{
		return (void*)(uint64_t)addr;
	}

	__attribute__((always_inline)) inline jlong address(void* ptr)
	{
		return (uint64_t)ptr;
	}
}

#endif//LYRA_CPP_TYPES
