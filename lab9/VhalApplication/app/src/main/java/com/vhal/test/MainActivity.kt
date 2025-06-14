package com.vhal.test

import android.app.Application
import android.car.Car
import android.car.hardware.CarPropertyValue
import android.car.hardware.property.CarPropertyManager
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    val  VENDOR_EXTENSION_LED_CONTROL_PROPERTY:Int = 0x21400104
    val areaID = 0
     lateinit var car:Car
     lateinit var carPropertyManager:CarPropertyManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        val btnOn = findViewById<Button>(R.id.btnOn)
        val btnOff = findViewById<Button>(R.id.btnOff)

        btnOff.setOnClickListener {
            setLedState(Integer(0))
        }
        btnOn.setOnClickListener {
            setLedState(Integer(1))
        }
        car = Car.createCar(this.applicationContext)
        if (car == null) {
            Log.e("Steering", "Failed to create Car instance")
        } else {
            carPropertyManager = car!!.getCarManager(Car.PROPERTY_SERVICE) as CarPropertyManager
            Log.e("Steering", "CarPropertyManager initialized successfully")
        }
    }
    fun setLedState(ledState:Integer) {
        try {
            synchronized(carPropertyManager) {

                carPropertyManager.setProperty(Integer::class.java, VENDOR_EXTENSION_LED_CONTROL_PROPERTY, areaID,ledState)
                Log.d("LedState", "led State changed to: ${ledState}")
            }

        } catch (e: Exception) {
            Log.e("LedState", "Error set Led State", e)
        }
    }
}