
#include <iostream>
#include <fstream>
#include <unistd.h>

#include <sys/system_properties.h>

#include <gpiohal.h>

#define GPIO_PIN 17

bool getLedServiceState()
{

    char value[PROP_VALUE_MAX];
    int ret = __system_property_get("reversegear", value);
    std::cout << ret <<"reversegear is  " << value << std::endl;
    return (ret > 0 && std::string(value) == "1");
}

int main()
{

    GpioHal gpio;
    // Export the GPIO pin if not already exported
    gpio.exportGpio(GPIO_PIN);

    // Set the pin direction to output
    gpio.setGpioDirection(GPIO_PIN, "out");

    // Toggle the GPIO pin in a loop
    for (;;)
    {

        if (getLedServiceState())
        {
            gpio.setGpioValue(GPIO_PIN, 1);
            std::cout << "led on GPIO " << GPIO_PIN << std::endl;
        }
        else
        {
            gpio.setGpioValue(GPIO_PIN, 0);
            std::cout << "led off GPIO " << GPIO_PIN << std::endl;
        }
        sleep(1); // Sleep for 1 second between toggles
    }
}