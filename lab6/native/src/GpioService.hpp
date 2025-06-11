#pragma once
#include "gpiohal.h"
#include <aidl/android/vendor/gpio/BnGpioService.h>
#include <android/binder_ibinder.h>

namespace aidl::android::vendor::gpio {

class GpioService : public aidl::android::vendor::gpio::BnGpioService {
public:
    GpioService() {
        // Constructor body (empty)
    }

    ~GpioService() {
        // Destructor body (empty)
    }

  ::ndk::ScopedAStatus setGpioState(int32_t in_pin, bool in_value, bool* _aidl_return) override;
  ::ndk::ScopedAStatus getGpioState(int32_t in_pin, bool* _aidl_return) override;
    
};

} // namespace aidl::android::vendor::gpio

