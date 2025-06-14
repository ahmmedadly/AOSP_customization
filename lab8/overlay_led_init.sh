#!/system/bin/sh

echo 17 > /sys/class/gpio/export
echo "out" > /sys/class/gpio/gpio17/direction
chmod 666 /sys/class/gpio/export
chmod 666 /sys/class/gpio/gpio17/direction
chmod 666 /sys/class/gpio/gpio17/value
