PRODUCT_PACKAGES += \
    fastbootd \
    gpiohal \
    # HelloWorld \
    # togleled \
    # ledtoggleshared \
    # inithelloservice \
    # initledservice  \
    # i2cbin \
    # libitochal \
    # i2cservice \
    

#   TestLogApp \
#     bgOverlaybp

BOARD_SEPOLICY_DIRS += \
    vendor/iti/lab5/sepolicy  \
    vendor/iti/lab6/sepolicy \
    vendor/iti/lab7/sepolicy

# PRODUCT_PACKAGE_OVERLAYS += bgOverlaybp
# PRODUCT_COPY_FILES += overlay_led_init.sh:vendor/bin/overlay_led_init