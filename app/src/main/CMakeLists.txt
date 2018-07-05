cmake_minimum_required(VERSION 3.4.1)

file(GLOB_RECURSE SOURCES "jni/*.c**")

add_library(${LIBRARY_NAME}
             SHARED
             ${SOURCES})

find_library(android-lib android)

target_link_libraries(
        ${LIBRARY_NAME}
        ${android-lib}
)