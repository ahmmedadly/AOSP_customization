#include "GpioService.hpp"
#include <chrono>
#include <ctime>
#include "gpiohal.h"
namespace aidl::android::vendor::gpio
{

    // ndk::ScopedAStatus Hello::getTime(std::string* _aidl_return) {
    //     auto now = std::chrono::system_clock::now();
    //     std::time_t now_c = std::chrono::system_clock::to_time_t(now);
    //     std::tm now_tm = *std::localtime(&now_c);
    //     char buf[9];
    //     strftime(buf, sizeof(buf), "%H:%M:%S", &now_tm);
    //     *_aidl_return = buf;
    //     return ndk::ScopedAStatus::ok();
    // }

    // ndk::ScopedAStatus Hello::getDate(std::string* _aidl_return) {
    //     auto now = std::chrono::system_clock::now();
    //     std::time_t now_c = std::chrono::system_clock::to_time_t(now);
    //     std::tm now_tm = *std::localtime(&now_c);
    //     char buf[11];
    //     strftime(buf, sizeof(buf), "%Y-%m-%d", &now_tm);
    //     *_aidl_return = buf;
    //     return ndk::ScopedAStatus::ok();
    // }

    ::ndk::ScopedAStatus GpioService::setGpioState(int32_t in_pin, bool in_value, bool *_aidl_return)
    {
        GpioHal gpio;
        // Export the GPIO pin if not already exported
        gpio.exportGpio(in_pin);

        // Set the pin direction to output
        gpio.setGpioDirection(in_pin, "out");
        if (gpio.setGpioValue(in_pin, in_value))
        {
            *_aidl_return = 1;
        }
        else
        {
            *_aidl_return = 0;
        }

        return ndk::ScopedAStatus::ok();
    }
    ::ndk::ScopedAStatus GpioService::getGpioState(int32_t in_pin, bool *_aidl_return)
    {

        GpioHal gpio;
        // Export the GPIO pin if not already exported
        int temp;
        gpio.getGpioValue(in_pin, &temp);

        if (temp == 0)
        {
            *_aidl_return = 0;
        }
        else
        {
            *_aidl_return = 1;
        }

        return ndk::ScopedAStatus::ok();
    }

}
