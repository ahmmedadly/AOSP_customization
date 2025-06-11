package android.vendor.gpio;

@VintfStability
interface IGpioService{
    boolean setGpioState(int pin , boolean value);
    boolean getGpioState(int pin);

}