#include "Hello.hpp"
#include <chrono>
#include <ctime>

namespace aidl::android::vendor::test {

ndk::ScopedAStatus Hello::getTime(std::string* _aidl_return) {
    auto now = std::chrono::system_clock::now();
    std::time_t now_c = std::chrono::system_clock::to_time_t(now);
    std::tm now_tm = *std::localtime(&now_c);
    char buf[9];
    strftime(buf, sizeof(buf), "%H:%M:%S", &now_tm);
    *_aidl_return = buf;  
    return ndk::ScopedAStatus::ok();
}

ndk::ScopedAStatus Hello::getDate(std::string* _aidl_return) {
    auto now = std::chrono::system_clock::now();
    std::time_t now_c = std::chrono::system_clock::to_time_t(now);
    std::tm now_tm = *std::localtime(&now_c);
    char buf[11];
    strftime(buf, sizeof(buf), "%Y-%m-%d", &now_tm);
    *_aidl_return = buf;  
    return ndk::ScopedAStatus::ok();
}

}
