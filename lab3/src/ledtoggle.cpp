
#include <iostream>
#include <fstream>
#include <unistd.h>

#include <inc/gpiohal.h>
#define GPIO_PIN 17
int main()
{

    GpioHal gpio;

    gpio.exportGpio(GPIO_PIN);


    gpio.setGpioDirection(GPIO_PIN, "out");

    bool valueled = true;


    for (int i = 0; i < 10; ++i)
    {

        if (valueled)
            gpio.setGpioValue(GPIO_PIN, 1);
        else
            gpio.setGpioValue(GPIO_PIN, 0);
        valueled = !valueled;

        std::cout << "Toggled GPIO " << GPIO_PIN << " (iteration " << i + 1 << ")" << std::endl;
        sleep(1); 
    }
}