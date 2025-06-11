#include "Hello.hpp"

#include <android-base/logging.h>
#include <android/binder_manager.h>
#include <android/binder_process.h>
#include <android/binder_ibinder.h>
#include <aidl/android/vendor/test/IHello.h> 

using aidl::android::vendor::test::Hello;

int main() {
    LOG(INFO) << "hello_service starting...";

    ABinderProcess_setThreadPoolMaxThreadCount(0);
    std::shared_ptr<aidl::android::vendor::test::Hello> hello = ndk::SharedRefBase::make<aidl::android::vendor::test::Hello>();
     if (!hello) {
        LOG(ERROR) << "Failed to create Hello service instance.";
        return EXIT_FAILURE;
    }
    const std::string instance = std::string() + aidl::android::vendor::test::Hello::descriptor + "/default";
    LOG(INFO) << "Registering service with name: " << instance;

    binder_status_t status = AServiceManager_addService(hello->asBinder().get(), instance.c_str());

        if (status == STATUS_OK) {
            LOG(INFO) << "Service registered successfully.";
        } else {
            LOG(ERROR) << "Failed to register service. Status: " << status;
            return EXIT_FAILURE;
        }
        ABinderProcess_joinThreadPool();
        return EXIT_FAILURE;
}
