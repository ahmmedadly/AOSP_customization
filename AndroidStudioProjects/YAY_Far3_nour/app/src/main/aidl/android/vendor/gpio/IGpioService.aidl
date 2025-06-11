// IGpioService.aidl
package android.vendor.gpio;

// Declare any non-default types here with import statements

interface IGpioService {
  boolean setGpioState(int pin, boolean value);
  boolean getGpioState(int pin);
}