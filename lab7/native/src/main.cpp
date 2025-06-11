#include "I2cService.hpp"
#include <android-base/logging.h>
#include <android/binder_manager.h>
#include <android/binder_process.h>
#include <android/binder_ibinder.h>
#include <aidl/android/vendor/i2c/II2cService.h> 

using aidl::android::vendor::i2c::II2cService;

int main() {
    LOG(INFO) << "hello i2cService starting...";
    ABinderProcess_setThreadPoolMaxThreadCount(0);

    std::shared_ptr<aidl::android::vendor::i2c::I2cService> i2cService = ndk::SharedRefBase::make<aidl::android::vendor::i2c::I2cService>();
    
    if (!i2cService) {
        LOG(ERROR) << "Failed to create i2cService  instance.";
        return EXIT_FAILURE;
    }
    const std::string instance = std::string() + aidl::android::vendor::i2c::I2cService::descriptor + "/default";
    LOG(INFO) << "Registering service with name: " << instance;

    binder_status_t status = AServiceManager_addService(i2cService->asBinder().get(), instance.c_str());
    

        if (status == STATUS_OK) {
            LOG(INFO) << "Service registered successfully.";
        } else {
            LOG(ERROR) << "Failed to register service. Status: " << status;
            return EXIT_FAILURE;
        }
        ABinderProcess_joinThreadPool();
        return EXIT_FAILURE;
}
