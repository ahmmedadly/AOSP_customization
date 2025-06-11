#include "GpioService.hpp"
#include "gpiohal.h"
#include <android-base/logging.h>
#include <android/binder_manager.h>
#include <android/binder_process.h>
#include <android/binder_ibinder.h>
#include <aidl/android/vendor/gpio/IGpioService.h> 

using aidl::android::vendor::gpio::IGpioService;

int main() {
    LOG(INFO) << "hello gpio_service starting...";
    ABinderProcess_setThreadPoolMaxThreadCount(0);

    std::shared_ptr<aidl::android::vendor::gpio::GpioService> gpioservice = ndk::SharedRefBase::make<aidl::android::vendor::gpio::GpioService>();
    
    if (!gpioservice) {
        LOG(ERROR) << "Failed to create gpio service instance.";
        return EXIT_FAILURE;
    }
    const std::string instance = std::string() + aidl::android::vendor::gpio::GpioService::descriptor + "/default";
    LOG(INFO) << "Registering service with name: " << instance;

    binder_status_t status = AServiceManager_addService(gpioservice->asBinder().get(), instance.c_str());

        if (status == STATUS_OK) {
            LOG(INFO) << "Service registered successfully.";
        } else {
            LOG(ERROR) << "Failed to register service. Status: " << status;
            return EXIT_FAILURE;
        }
        ABinderProcess_joinThreadPool();
        return EXIT_FAILURE;
}
