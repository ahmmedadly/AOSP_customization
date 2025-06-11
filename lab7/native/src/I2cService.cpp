#include "I2cService.hpp"
#include <chrono>
#include <ctime>
#include <iostream>
#include <unistd.h>
#include <fcntl.h>
#include <sys/ioctl.h>
#include <linux/i2c-dev.h>
#include <stdexcept>
 namespace aidl::android::vendor::i2c
{

    ::ndk::ScopedAStatus I2cService::getWhealAngle(int32_t* _aidl_return)
    {
        const char *device = "/dev/i2c-1"; // I2C bus
        int file = open_i2c(device, ADS1115_ADDRESS);
        if (file < 0)
            return ndk::ScopedAStatus::ok();

        int channel = 1;
        configure_ads1115(file, channel);

        usleep(10000);

        int16_t value = read_ads1115(file); // Read ADC value
        std::cout << "ADC Value for channel " << channel << ": " << value << std::endl;
        *_aidl_return = value;
        close(file);
        return ndk::ScopedAStatus::ok();
    }
}
