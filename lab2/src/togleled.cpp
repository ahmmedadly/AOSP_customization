#include <iostream>
#include <fstream>
#include <unistd.h>

#define GPIO_PIN 17
#define GPIO_PATH "/sys/class/gpio/"

void export_gpio(int pin) {
    std::ofstream export_file(GPIO_PATH "export");
    if (export_file.is_open()) {
        export_file << pin;
        export_file.close();
    } else {
        std::cerr << "Failed to export GPIO " << pin << std::endl;
    }
}

void set_gpio_direction(int pin, const std::string& direction) {
    std::ofstream direction_file(GPIO_PATH "gpio" + std::to_string(pin) + "/direction");
    if (direction_file.is_open()) {
        direction_file << direction;
        direction_file.close();
    } else {
        std::cerr << "Failed to set direction for GPIO " << pin << std::endl;
    }
}

void toggle_gpio(int pin) {
    std::ofstream value_file(GPIO_PATH "gpio" + std::to_string(pin) + "/value");
    if (value_file.is_open()) {
        // Toggle the value (this example toggles between 0 and 1)
        static bool pin_state = false;
        value_file << (pin_state ? "0" : "1");
        value_file.close();
        pin_state = !pin_state;
    } else {
        std::cerr << "Failed to write value to GPIO " << pin << std::endl;
    }
}

int main() {
    // Export the GPIO pin if not already exported
    export_gpio(GPIO_PIN);

    // Set the pin direction to output
    set_gpio_direction(GPIO_PIN, "out");

    // Toggle the GPIO pin in a loop
    for (int i = 0; i < 10; ++i) {
        toggle_gpio(GPIO_PIN);
        std::cout << "Toggled GPIO " << GPIO_PIN << " (iteration " << i + 1 << ")" << std::endl;
        sleep(1);  // Sleep for 1 second between toggles
    }

}