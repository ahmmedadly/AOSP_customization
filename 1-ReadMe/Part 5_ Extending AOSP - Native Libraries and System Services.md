# Part 5: Extending AOSP - Native Libraries and System Services

This section covers how to extend the Android Open Source Project (AOSP) by adding native shared libraries and registering custom system services. These are advanced topics crucial for deep system customization and integration of new hardware functionalities.

## ✅ 1. Adding a Native Shared Library (.so) to AOSP

Native shared libraries (`.so` files) are essential for performance-critical code, integrating with existing C/C++ codebases, or interacting directly with hardware. In AOSP, these libraries are typically built using the `cc_library` or `cc_library_shared` module types in `Android.bp`.

### 📁 Folder structure:

It's common practice to place new native libraries under the `external/` directory or within a vendor-specific directory. Let's assume `external/mylib/`:

```
external/mylib/
├── Android.bp   # Soong build file for your library
├── mylib.c      # Your C source file
├── mylib.h      # Your C header file
└── Android.mk   # (Optional) If you still need a legacy Makefile
```

### 🧾 `Android.bp` for a Shared Library:

Create an `Android.bp` file in `external/mylib/` with the following content:

```bp
cc_library_shared {
    name: "libmylib", // The name of your shared library (will result in libmylib.so)
    srcs: ["mylib.c"], // List of source files
    // Optional: Add other source files like "mylib2.cpp", "src/utils.c", etc.

    shared_libs: [ // List of shared libraries this library depends on
        // "liblog", // Example: if you use Android's logging (ALOG*)
        // "libutils", // Example: if you use Android's utility functions
    ],
    // static_libs: [ // List of static libraries this library depends on
    //     "libmystaticlib",
    // ],

    export_include_dirs: ["."], // Directories to export as include paths for other modules
                               // This allows other modules to #include "mylib.h"

    cflags: [ // Compiler flags
        "-Wall", // Enable all warnings
        "-Werror", // Treat warnings as errors
        // "-DDEBUG_MODE", // Example: define a preprocessor macro
    ],

    vendor: true, // If true, installs the library to the /vendor partition.
                  // Use `system_ext_specific: true` for /system_ext.
                  // If neither, it defaults to /system/lib or /system/lib64.

    // For JNI libraries that will be loaded by Java code:
    // jni_libs: ["libyourjni"],

    // If your library is for a specific architecture:
    // target: {
    //     android: {
    //         arch: "arm64",
    //     },
    // },
}
```

**Explanation of Attributes:**

*   **`cc_library_shared`**: This module type defines a shared C/C++ library (`.so`). Use `cc_library_static` for static libraries (`.a`).
*   **`name`**: The name of the module. The output file will be `lib<name>.so`.
*   **`srcs`**: Specifies the source files to compile.
*   **`shared_libs` / `static_libs`**: Declare dependencies on other shared or static libraries. The build system will ensure these are linked correctly.
*   **`export_include_dirs`**: Crucial for making your library's header files available to other modules that depend on it. If your headers are in a `include/` subdirectory, you'd put `["include"]` here.
*   **`cflags`**: Compiler flags to apply during compilation.
*   **`vendor` / `system_ext_specific`**: Determines the installation path of the library on the device. `vendor: true` puts it in `/vendor/lib` (or `lib64`), which is common for HALs and vendor-specific components.

### 🛠️ To use `libmylib` in an app (or another native module):

If you have an Android app (defined by `android_app` in `Android.bp`) or another native module that needs to link against `libmylib`, you add it to their `shared_libs` property:

*   In your app’s `Android.bp` (e.g., `packages/apps/YourApp/Android.bp`):

    ```bp
    android_app {
        name: "YourApp",
        // ... other attributes ...
        shared_libs: [
            "libmylib", // Add your shared library here
            // "liblog",
        ],
        // ...
    }
    ```

### 📦 Add to product:

For `libmylib` to be included in the final system image, you must add it to the `PRODUCT_PACKAGES` list in your device's `device.mk` file (e.g., `device/<vendor>/<device>/device.mk`):

```make
PRODUCT_PACKAGES += \
    libmylib \
    # Other modules...
```

### ✅ Build:

After making these changes, you can build your library and the dependent modules:

```bash
source build/envsetup.sh
lunch <your_target>
m libmylib # Build just your library
m YourApp  # Build your app (if it depends on libmylib)
m          # Or a full build to ensure everything is included
```

## ✅ 2. Registering a Custom System Service

Android's core functionality is provided by a multitude of system services (e.g., `PackageManager`, `ActivityManager`, `WindowManager`). If you need to add new system-level functionality that is accessible across different processes, you'll likely need to register your own custom system service. This involves extending the Android **framework** itself.

### 🔧 Example: `MySystemService`

Let's create a simple custom system service `MySystemService` that can be accessed by other applications or framework components.

#### 📁 Files:

Custom system services typically reside within the `frameworks/base/services/core/java/com/android/server/` directory.

*   **Java Service Implementation**: `frameworks/base/services/core/java/com/android/server/MySystemService.java`
*   **Service Registration**: You'll modify `SystemServer.java` to start your service during the Android boot process.

#### 📄 `MySystemService.java`:

This is the actual implementation of your service. It will extend `android.content.Context` and provide the methods you want to expose.

```java
package com.android.server;

import android.content.Context;
import android.util.Slog;
import android.os.IBinder; // Required for Binder service
import android.os.ServiceManager; // Required for registering service

// (Optional) If you define an AIDL interface for your service, you would implement it here.
// For simplicity, we'll start without an AIDL interface and add it later if needed.

public class MySystemService {
    private static final String TAG = "MySystemService";
    private final Context mContext;

    public MySystemService(Context context) {
        mContext = context;
        Slog.i(TAG, "MySystemService: Service created");
    }

    /**
     * Called by SystemServer to start the service.
     * This is where you would register your service with ServiceManager.
     */
    public void start() {
        Slog.i(TAG, "MySystemService: Service starting...");

        // Register your service with the ServiceManager
        // This makes your service discoverable by other processes via its name.
        ServiceManager.addService(Context.MY_SYSTEM_SERVICE, new MySystemServiceBinder());

        Slog.i(TAG, "MySystemService: Service started and registered.");
    }

    /**
     * Example method exposed by your service.
     * This method could be called by other components.
     */
    public void doSomethingCool(String action) {
        Slog.d(TAG, "MySystemService: Doing something cool: " + action);
        // Implement your service logic here
    }

    /**
     * Inner class that implements the Binder interface for your service.
     * This is what other processes will interact with.
     */
    private final class MySystemServiceBinder extends IMySystemService.Stub { // Assuming IMySystemService.aidl exists
        @Override
        public void doSomethingCool(String action) {
            // Delegate to the outer class's method
            MySystemService.this.doSomethingCool(action);
        }

        // Add other methods from your AIDL interface here
    }

    // Define a constant for your service name in Context.java or a similar central place
    // For demonstration, let's assume it's defined elsewhere or use a string literal.
    // In a real AOSP project, you'd add: public static final String MY_SYSTEM_SERVICE = "mysystemservice";
}
```

**Note**: In a real AOSP scenario, you would typically define an AIDL interface (`IMySystemService.aidl`) for your service to allow other processes to communicate with it. The `MySystemServiceBinder` would then implement the `IMySystemService.Stub` generated from that AIDL. For this example, we've assumed `IMySystemService.Stub` exists for demonstration purposes. We will cover AIDL in more detail in a later section.

#### 📄 Modify `SystemServer.java`:

`SystemServer.java` is the main entry point for all core Android system services. You need to add code here to instantiate and start your `MySystemService` during the boot process.

Located at:

```
frameworks/base/services/java/com/android/server/SystemServer.java
```

1.  **Add the import statement** at the top of the file:

    ```java
    import com.android.server.MySystemService;
    ```

2.  **Instantiate and start your service** inside the `startOtherServices()` method. This method is called after many essential services are already running.

    ```java
    // Inside startOtherServices() method
    try {
        Slog.i(TAG, 

