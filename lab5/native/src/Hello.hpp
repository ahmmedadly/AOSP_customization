#pragma once

#include <aidl/android/vendor/test/BnHello.h>
#include <android/binder_ibinder.h>

namespace aidl::android::vendor::test {

class Hello : public aidl::android::vendor::test::BnHello {
public:
    Hello() {
        // Constructor body (empty)
    }

    ~Hello() {
        // Destructor body (empty)
    }

    ::ndk::ScopedAStatus getTime(std::string* _aidl_return) override;
    ::ndk::ScopedAStatus getDate(std::string* _aidl_return) override;
    
};

} // namespace aidl::android::vendor::test

