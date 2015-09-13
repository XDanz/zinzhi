cmake_minimum_required(VERSION 3.3)
project(tree)

set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -std=c++11  -Wall")
set(CMAKE_CXX_FLAGS_DEBUG "${CMAKE_C_FLAGS_DEBUG} -dwarf-2")
set(CMAKE_BINARY_DIR ${CMAKE_SOURCE_DIR}/objs)
set(EXECUTABLE_OUTPUT_PATH ${CMAKE_BINARY_DIR})

load_cache()
include_directories(include)
set(SOURCE_FILES
        src/tree.cpp
        test/test.cpp
        include/Tree.h
)



add_executable(tree ${SOURCE_FILES})
MESSAGE( STATUS "CMAKE_BUILD_TYPE: " ${CMAKE_BUILD_TYPE} )

MESSAGE( STATUS "CMAKE_COMPILER_IS_GNUCXX : " ${CMAKE_COMPILER_IS_GNUCXX} )
