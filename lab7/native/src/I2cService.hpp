#pragma once
#include "hali2c.h"
#include <aidl/android/vendor/i2c/BnI2cService.h>

#include <android/binder_ibinder.h>

namespace aidl::android::vendor::i2c {

class I2cService : public aidl::android::vendor::i2c::BnI2cService {
public:
    I2cService() {
        // Constructor body (empty)
    }

    ~I2cService() {
        // Destructor body (empty)
    }

  ::ndk::ScopedAStatus getWhealAngle(int32_t* _aidl_return ) override;
    
};

} // namespace aidl::android::vendor::gpio

