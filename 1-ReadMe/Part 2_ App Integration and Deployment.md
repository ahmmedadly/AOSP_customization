# Part 2: App Integration and Deployment

This section focuses on how applications are integrated into the Android Open Source Project (AOSP) build, covering different scenarios for deploying and testing your apps within the AOSP environment. Understanding these methods is crucial for developers working on system-level applications or custom ROMs.

## 📦 Build vs Push: When to Do What

When developing applications for AOSP, you often face a choice: should you rebuild the entire system image to include your app, or can you simply push it to a running device? The answer depends on the nature of your changes and the type of application. This table provides a quick guide.

| Task                                                | Use `adb push`                                     | Use `mm/mmm` (Module Build)          | Use `m / full rebuild` (Full System Build) |
| :-------------------------------------------------- | :------------------------------------------------- | :----------------------------------- | :----------------------------------------- |
| Minor app or Java code change (e.g., UI tweaks)     | ✅ **Recommended** (Push APK directly)             | ✅ (Build app module only)           | ❌ (Overkill)                              |
| Native library (.so) change (e.g., JNI updates)     | ❌ (Requires system access/permissions)            | ✅ (Build native module)             | ⚠️ (May need partial rebuild/reflash)      |
| Framework/SDK change (e.g., new API in `frameworks/base`) | ❌ (Core system component)                         | ❌ (Requires system-level changes)   | ✅ **Required** (Full build and flash)     |
| System image modification (e.g., `/system` partition changes) | ❌ (Cannot modify read-only partition directly)    | ❌ (Does not generate system image)  | ✅ **Required** (Full build and flash)     |
| Kernel change (e.g., new driver)                    | ❌ (Low-level system component)                    | ❌ (Not applicable)                  | ✅ **Required** (Rebuild `boot.img`)       |

**Explanation of Methods:**

*   **`adb push`**: This is the fastest method for quick iteration on user-space applications. You compile your APK outside the AOSP build system (e.g., using Android Studio) and then push it to a device. This is ideal for testing app logic, UI changes, or features that don't require deep system integration.
*   **`mm`/`mmm`**: These commands are part of the AOSP build system and allow you to build specific modules (like an app, a library, or a HAL) without compiling the entire AOSP. This is efficient when your changes are confined to a particular module and you want to integrate it into the AOSP build process, but don't need a full system image.
*   **`m`/`full rebuild`**: This is necessary when you make changes to core system components, framework services, or anything that affects the `/system` or `/vendor` partitions. A full rebuild generates new system images (`system.img`, `vendor.img`, `boot.img`, etc.) that then need to be flashed onto the device.

## 🔄 Injecting/Adding an App or Module to the Image

When you want your application to be a permanent part of the AOSP system image, rather than just a user-installed app, you need to integrate it into the AOSP build process. There are two primary ways to achieve this.

### ✅ Option 1: Prebuilt APK Injection (for system image)

This method is used when you have a pre-compiled APK (e.g., from a third-party vendor or a separate Android Studio project) that you want to include directly into the AOSP system image. This is common for proprietary applications or system apps that don't have their source code within the AOSP tree.

1.  **Place your `.apk`**: Create a dedicated directory for your prebuilt APK within your AOSP source tree. A common location is `vendor/<your_vendor>/prebuilts/app/MyApp/`.
    *   Example: `vendor/google/prebuilts/app/YouTube/YouTube.apk`

2.  **Define the module with `Android.mk` or `Android.bp`**: You need to tell the AOSP build system about your APK. While `Android.bp` is the modern standard, `Android.mk` is often used for prebuilts due to its simplicity for this specific use case.

    **Using `Android.mk` (Recommended for Prebuilts):**
    Create an `Android.mk` file in the same directory as your APK (e.g., `vendor/<your_vendor>/prebuilts/app/MyApp/Android.mk`) with the following content:

    ```make
    LOCAL_PATH := $(call my-dir)
    include $(CLEAR_VARS)

    LOCAL_MODULE := MyApp # The name of your module
    LOCAL_SRC_FILES := MyApp.apk # Path to your APK relative to LOCAL_PATH
    LOCAL_MODULE_CLASS := APPS # This specifies it's an application
    LOCAL_MODULE_TAGS := optional # Can be 'optional' or 'debug' or 'eng'
    LOCAL_CERTIFICATE := PRESIGNED # Use the certificate already in the APK
    LOCAL_MODULE_PATH := $(TARGET_OUT)/app # Or $(TARGET_OUT)/priv-app for privileged apps
    LOCAL_UNINSTALLABLE_MODULE := true # Prevents user from uninstalling

    include $(BUILD_PREBUILT)
    ```

    *   **`LOCAL_MODULE`**: The name by which your app will be known in the build system.
    *   **`LOCAL_SRC_FILES`**: Specifies the APK file to include.
    *   **`LOCAL_MODULE_CLASS := APPS`**: Declares this module as an application.
    *   **`LOCAL_MODULE_TAGS := optional`**: Means it's an optional component, not essential for the core system.
    *   **`LOCAL_CERTIFICATE := PRESIGNED`**: Important! This tells the build system that the APK is already signed and should not be re-signed. If your APK is not signed, you might use `platform`, `shared`, `media`, or `testkey` to sign it with AOSP's built-in keys.
    *   **`LOCAL_MODULE_PATH := $(TARGET_OUT)/app`**: Defines where the app will be installed on the device. Use `$(TARGET_OUT)/priv-app` for privileged system apps that require special permissions.
    *   **`LOCAL_UNINSTALLABLE_MODULE := true`**: Makes the app non-uninstallable by the user, typical for system apps.

3.  **Add the module to `PRODUCT_PACKAGES`**: You need to explicitly tell your device's product configuration to include your new module. Edit your `device.mk` file (e.g., `device/<vendor>/<device>/device.mk`).

    ```make
    PRODUCT_PACKAGES += \
        MyApp # Add your module name here
    ```

4.  **Run `m` to rebuild the system image**: After making these changes, you must perform a full AOSP build to integrate your app into the system image.

    ```bash
    source build/envsetup.sh
    lunch <your_target>
    m -jN # Rebuild the entire AOSP
    ```

    *   **Result**: Your app will be installed in the specified path (e.g., `/system/app/MyApp` or `/system/priv-app/MyApp`) on the device after flashing the new system image.

### ✅ Option 2: Just Push to Device (for testing)

This method is primarily for quick testing and development iterations where you don't need your app to be part of the system image. It allows you to install your app directly onto a running Android device (or emulator) using `adb`.

```bash
adb root             # Gain root access on the device (if not already root)
adb remount          # Remount the /system partition as writable
adb push MyApp.apk /system/app/MyApp/MyApp.apk # Push your APK to a system location
adb shell chmod 644 /system/app/MyApp/MyApp.apk # Set correct permissions
adb reboot           # Reboot the device for changes to take effect
```

*   **Prerequisites**: Your device must be rooted, and you need to be able to `adb remount` the `/system` partition as writable. This is often possible on development devices or emulators but not on production devices.
*   **Permissions**: Setting `chmod 644` ensures the APK has read permissions for all and write permissions for the owner, which is necessary for the Android system to recognize and install it.
*   **Reboot**: A reboot is typically required for the Android system to scan the new APK in the `/system` partition and make it available.
*   **Limitations**: This method is temporary. If you factory reset the device or flash a new system image, your pushed app will be gone. It's not a permanent integration into the AOSP build.

**When to use this method:**
*   Rapid prototyping and testing of app features.
*   Debugging an app that needs to run as a system app but you don't want to rebuild AOSP for every small change.
*   When you don't have the full AOSP source code or the ability to rebuild it.

**Alternative for user apps:** For regular user applications that don't need system privileges, a simple `adb install MyApp.apk` is sufficient. This installs the app to the `/data/app` partition, and it can be uninstalled by the user.


