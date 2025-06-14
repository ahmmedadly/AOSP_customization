# Deploying Android App as a Privileged System App

This guide explains how to push a debug APK to an Android device's `/system/priv-app` directory to run as a privileged app.

---

## 📦 Step 1: Build APK from Android Studio

1. Open your project in **Android Studio**.
2. Select **Build > Build APK(s)** from the top menu.
3. After building, Android Studio will show you a notification with a link to the `APK` location.
4. You can take a copy of this file and place it in your AOSP directory, for example: "vendor/iti/lab9/app-debug.apk"


---

## 🔧 Step 2: Push the APK to `/system/priv-app`

> ⚠️ **Make sure your device is rooted and uses a `userdebug` build.**

Run the following commands:

```bash
adb root
adb shell mount -o remount,rw /
adb push vendor/iti/lab9/app-debug.apk /system/priv-app
adb shell
reboot